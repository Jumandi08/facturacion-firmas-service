# Feature Specification: Registrar cliente con fecha de vencimiento de firma digital

**Feature Branch**: `001-registrar-cliente-vencimiento`

**Created**: 2026-07-05

**Status**: Draft

**Input**: User description: "Registrar cliente con fecha de vencimiento de firma digital. Como Proveedor/Gestor de firmas digitales, quiero registrar la fecha de vencimiento de la firma de un cliente nuevo, para que el sistema lo incluya en el seguimiento automático de vencimientos desde el primer día."

**Trazabilidad**: origen en `discoveries/facturacion` (Unidad 1 — MVP Canvas "Gestor de
vencimientos de firmas digitales", funcionalidad-mínima-1) y `deliveries/facturacion`
(Unidad 2 — historia **US-04**, épica **E-02 "Cartera con dato de vencimiento
confiable"**, 3 puntos, sin dependencias, formato INVEST validado por el gate
DoR/INVEST del Agile Delivery Team).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar un cliente nuevo con su fecha de vencimiento (Priority: P1)

Como Proveedor/Gestor de firmas digitales, quiero registrar el nombre de un cliente
y la fecha de vencimiento de su firma, para que el sistema lo incorpore de inmediato
al seguimiento de cartera sin depender de una hoja de cálculo o memoria manual.

**Why this priority**: Es el habilitador de todo el MVP (épica E-02). Sin este dato
registrado no existen alertas (US-02/US-03) ni tablero de estados (US-01). Es la
historia de menor tamaño (3 pts) y sin dependencias, por lo que constituye el primer
incremento demostrable.

**Independent Test**: Se puede probar de forma aislada enviando una solicitud de alta
con nombre y fecha de vencimiento válidos, y verificando que el cliente queda
persistido con un identificador único y un estado calculado.

**Acceptance Scenarios**:

1. **Given** que el Proveedor/Gestor va a dar de alta un cliente, **When** ingresa su
   nombre y su fecha de vencimiento de firma, **Then** el sistema lo agrega a la
   cartera con un identificador único y lo deja disponible para el seguimiento
   automático (alertas y tablero) según el parámetro N (días de anticipación,
   configurable, valor por defecto 15 días, mínimo 7 días).
2. **Given** un cliente recién registrado, **When** se consulta el listado de
   cartera, **Then** aparece con su estado calculado a partir de la fecha de
   vencimiento ingresada: `AL_DIA` (faltan más de N días), `POR_VENCER` (faltan N
   días o menos y no ha vencido) o `VENCIDA` (la fecha ya pasó o es hoy).

---

### User Story 2 - Rechazar el alta cuando falta la fecha de vencimiento (Priority: P2)

Como Proveedor/Gestor, quiero que el sistema me impida guardar un cliente sin fecha
de vencimiento, para no tener cartera con datos incompletos que rompan el
seguimiento automático más adelante.

**Why this priority**: Protege la integridad del dato habilitador (E-02); sin esta
validación, US-01/US-02/US-03 heredarían clientes sin fecha calculable. Es
complementaria a la Historia 1 pero no bloquea su valor principal.

**Independent Test**: Se puede probar de forma aislada enviando una solicitud de
alta sin fecha de vencimiento (o con fecha vacía/nula) y verificando que el sistema
la rechaza con un error de validación y no persiste ningún registro.

**Acceptance Scenarios**:

1. **Given** un formulario de alta de cliente, **When** el campo de fecha de
   vencimiento llega vacío o nulo, **Then** el sistema responde con un error de
   validación (HTTP 400) que identifica el campo faltante y no guarda el cliente.
2. **Given** un formulario de alta de cliente, **When** el nombre llega vacío o
   compuesto solo de espacios en blanco, **Then** el sistema responde con un error
   de validación (HTTP 400) que identifica el campo faltante y no guarda el
   cliente.

---

### User Story 3 - Consultar la cartera de clientes registrados (Priority: P3)

Como Proveedor/Gestor, quiero consultar el listado completo de clientes
registrados con su estado de vencimiento, para tener visibilidad de mi cartera
incluso antes de que exista el tablero visual completo (US-01).

**Why this priority**: Expone el dato que US-01 (tablero) consumirá; sin embargo,
el valor de negocio principal ya se cumple con las Historias 1 y 2 — esta historia
solo da visibilidad de lectura sobre lo ya registrado.

**Independent Test**: Se puede probar de forma aislada consultando el listado de
clientes cuando no hay ninguno registrado (debe devolver una lista vacía) y luego
tras registrar clientes con distintas fechas (debe reflejar el estado correcto de
cada uno).

**Acceptance Scenarios**:

1. **Given** que no hay clientes registrados, **When** se consulta el listado de
   cartera, **Then** el sistema responde con una lista vacía (HTTP 200), no con un
   error.
2. **Given** varios clientes registrados con fechas de vencimiento distintas,
   **When** se consulta el listado de cartera, **Then** cada cliente aparece con su
   nombre, fecha de vencimiento y estado (`AL_DIA` / `POR_VENCER` / `VENCIDA`)
   calculado de forma consistente con el mismo parámetro N.

---

### Edge Cases

- **Fecha de vencimiento ya pasada**: registrar un cliente cuya fecha de
  vencimiento es anterior a hoy debe dejarlo inmediatamente en estado `VENCIDA`
  (se permite explícitamente, para reflejar carga de cartera existente ya vencida).
- **Fecha de vencimiento igual a hoy**: se considera `VENCIDA`, no `POR_VENCER` —
  el corte es estrictamente "fecha futura" para no vencida.
- **Nombre en blanco o solo espacios**: debe rechazarse como si estuviera vacío
  (ver User Story 2, escenario 2).
- **Cartera vacía**: consultar el listado sin clientes registrados debe devolver
  una lista vacía, nunca un error 404 o 500.
- **Múltiples clientes con la misma fecha de vencimiento**: deben registrarse
  todos de forma independiente, cada uno con su propio identificador único.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE permitir registrar un cliente nuevo con nombre y
  fecha de vencimiento de firma digital, asignándole un identificador único.
- **FR-002**: El sistema DEBE rechazar el alta de un cliente cuando la fecha de
  vencimiento es nula o está vacía, respondiendo con un error de validación que
  identifique el campo.
- **FR-003**: El sistema DEBE rechazar el alta de un cliente cuando el nombre es
  nulo, vacío o contiene solo espacios en blanco, respondiendo con un error de
  validación que identifique el campo.
- **FR-004**: El sistema DEBE calcular y exponer, para cada cliente registrado, un
  estado derivado de su fecha de vencimiento: `AL_DIA` si faltan más de N días,
  `POR_VENCER` si faltan N días o menos (y la fecha aún no llegó), o `VENCIDA` si
  la fecha ya pasó o es hoy.
- **FR-005**: El parámetro N (días de anticipación para el estado `POR_VENCER`)
  DEBE ser una constante de configuración del sistema (valor por defecto 15 días,
  mínimo permitido 7 días), no un valor ingresado por cliente.
- **FR-006**: El sistema DEBE permitir registrar clientes cuya fecha de vencimiento
  ya pasó (carga de cartera existente), dejándolos en estado `VENCIDA` desde el
  registro.
- **FR-007**: El sistema DEBE permitir consultar el listado completo de clientes
  registrados junto con su estado calculado, devolviendo una lista vacía (no un
  error) cuando no hay clientes.
- **FR-008**: El sistema NO DEBE implementar en esta funcionalidad el envío de
  alertas (US-02/US-03), la vista de tablero visual completa (US-01), ni edición,
  eliminación o autenticación de usuarios — quedan fuera de alcance de esta
  historia por decisión explícita del backlog (Unidad 2).

### Key Entities *(include if feature involves data)*

- **Cliente**: representa a un cliente del Proveedor/Gestor cuya firma digital
  debe seguirse. Atributos clave: identificador único, nombre, fecha de
  vencimiento de la firma. El estado (`AL_DIA` / `POR_VENCER` / `VENCIDA`) es un
  valor calculado a partir de la fecha de vencimiento y el parámetro N, no un dato
  almacenado de forma independiente que pueda desincronizarse.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Un Proveedor/Gestor puede registrar un cliente nuevo con su fecha de
  vencimiento en una única interacción, sin pasos adicionales de configuración por
  cliente.
- **SC-002**: El 100% de los intentos de alta sin nombre o sin fecha de
  vencimiento son rechazados con un mensaje que identifica exactamente el campo
  faltante, sin excepciones no controladas.
- **SC-003**: El estado de cada cliente en la cartera (`AL_DIA` / `POR_VENCER` /
  `VENCIDA`) es siempre coherente con la fecha de vencimiento registrada y el
  parámetro N vigente, verificable en el 100% de los clientes consultados.
- **SC-004**: Consultar la cartera de clientes responde en menos de 1 segundo con
  hasta 500 clientes registrados (volumen esperado de la cartera de un
  Proveedor/Gestor individual, según el MVP Canvas: 10–100+ clientes).

## Assumptions

- El parámetro N (días de anticipación) es único y global para todo el sistema en
  esta primera funcionalidad; no se contempla un N distinto por cliente o por
  Proveedor/Gestor (eso queda para una iteración posterior si el negocio lo
  requiere).
- No existe todavía un modelo de autenticación/autorización de usuarios: esta
  funcionalidad expone el registro y consulta de clientes sin control de acceso,
  ya que la historia US-04 no lo exige y el MVP Canvas no lo define como
  requisito mínimo. Se documenta como deuda conocida, no como hallazgo de
  seguridad nuevo.
- La persistencia se resuelve con una base de datos relacional embebida (H2) para
  esta etapa del MVP, consistente con el resto de funcionalidades del curso.
- "Firma digital" y "cliente" se usan aquí en el sentido de negocio del MVP
  Canvas (cartera de clientes con firmas electrónicas activas que gestiona un
  Proveedor/Gestor), no en el sentido criptográfico de una firma digital de
  documentos.
