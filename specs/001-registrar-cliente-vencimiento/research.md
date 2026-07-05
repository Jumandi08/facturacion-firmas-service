# Research: Registrar cliente con fecha de vencimiento de firma digital

## Decisión: Cálculo de estado como método de dominio, no columna persistida

**Decisión**: `EstadoCliente` se calcula en tiempo de lectura a partir de
`fechaVencimiento` y la constante N; no se almacena como columna en la base de
datos.

**Rationale**: si se persistiera el estado, cada cambio de día requeriría un job
de recálculo para mantenerlo sincronizado, introduciendo una fuente de
inconsistencia (fecha vs. estado desincronizados). Calcularlo en el dominio
(`Cliente.calcularEstado(LocalDate hoy, int diasAnticipacionN)`) garantiza que el
estado siempre es coherente con la fecha real, sin job adicional — consistente
con el principio V (Simplicidad/YAGNI) de la constitución.

**Alternatives considered**: columna `estado` persistida + scheduled job diario
de recálculo. Rechazada: añade infraestructura (scheduler) fuera del alcance de
US-04 y un vector de inconsistencia que no aporta valor en este MVP.

## Decisión: Parámetro N como constante de configuración (`application.properties`)

**Decisión**: N se expone como propiedad `app.vencimiento.dias-anticipacion-n`
con valor por defecto `15`, validado en el arranque para no admitir menos de 7
(FR-005). Se inyecta en el dominio vía el caso de uso, no vía variable estática.

**Rationale**: el spec (FR-005) exige que N sea una constante de sistema, no un
dato por cliente. Usar `application.properties` permite cambiarlo sin recompilar
y es el mecanismo estándar de Spring Boot para configuración.

**Alternatives considered**: hardcodear N=15 en el dominio. Rechazada: dificulta
ajustar el valor sin tocar código y no permite validar el mínimo de 7 días de
forma centralizada.

## Decisión: H2 en memoria con `ddl-auto=create-drop`

**Decisión**: usar H2 en memoria, con Hibernate generando el esquema
automáticamente al arrancar (`spring.jpa.hibernate.ddl-auto=create-drop`).

**Rationale**: el spec y el MVP no exigen persistencia productiva ni migraciones
versionadas todavía; es el mismo enfoque simple usado en el resto del curso.
Introducir Flyway/Liquibase para una única entidad sería una abstracción
prematura (principio V).

**Alternatives considered**: Flyway con migraciones SQL versionadas. Rechazada
para esta funcionalidad por desproporción de esfuerzo vs. alcance; puede
adoptarse en una iteración posterior si el proyecto crece.

## Decisión: Validación con Bean Validation (`jakarta.validation`)

**Decisión**: `RegistrarClienteRequest` usa `@NotBlank` para el nombre y
`@NotNull` para la fecha de vencimiento; `GlobalExceptionHandler` traduce
`MethodArgumentNotValidException` a HTTP 400 con el detalle del campo.

**Rationale**: es el mecanismo estándar de Spring para validación declarativa en
el borde HTTP, cumple FR-002/FR-003 sin lógica de validación manual dispersa.

**Alternatives considered**: validación manual en el controller. Rechazada: más
código repetitivo y menos consistente que las anotaciones estándar.

## Sin incógnitas pendientes

No quedan marcadores `NEEDS CLARIFICATION` en el Technical Context: todas las
decisiones técnicas se resolvieron con defaults razonables documentados en
Assumptions (spec.md) y en este research.
