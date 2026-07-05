# facturacion-firmas-service

Servicio backend que implementa la historia de usuario **US-04 · Registrar
cliente con fecha de vencimiento de firma digital**, desarrollada con
**Spec-Driven Development (SDD)** usando [GitHub Spec-Kit](https://github.com/github/spec-kit),
como el componente de "Nuevo desarrollo" del Proyecto Final **Autonomous
Software Factory** de la asignatura Ingeniería de Software — Maestría en
Software.

Este repositorio es el tercer eslabón de un pipeline de tres agentes: no
parte de cero, sino que **reutiliza sin rehacer** los resultados reales de
las Unidades 1 y 2 del curso, y es auditado por el Quality Agent de la
Unidad 3.

## Tabla de contenidos

- [Producto y contexto de negocio](#producto-y-contexto-de-negocio)
- [Trazabilidad del pipeline (Unidad 1 → 2 → 3)](#trazabilidad-del-pipeline-unidad-1--2--3)
- [Historia implementada: US-04](#historia-implementada-us-04)
- [Stack y arquitectura](#stack-y-arquitectura)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Modelo de dominio](#modelo-de-dominio)
- [API REST](#api-rest)
- [Artefactos de Spec-Kit (SDD)](#artefactos-de-spec-kit-sdd)
- [Cómo ejecutar el servicio](#cómo-ejecutar-el-servicio)
- [Cómo ejecutar las pruebas y la cobertura](#cómo-ejecutar-las-pruebas-y-la-cobertura)
- [Quality Gate (Unidad 3)](#quality-gate-unidad-3)
- [Decisiones y deuda técnica declarada](#decisiones-y-deuda-técnica-declarada)
- [Historial de commits relevantes](#historial-de-commits-relevantes)

## Producto y contexto de negocio

**Producto**: Gestor de vencimientos de firmas digitales — una herramienta
para que un Proveedor/Gestor de firmas digitales lleve el control de cuándo
vence la firma de cada uno de sus clientes, en lugar de depender de una hoja
de cálculo o de la memoria manual.

Esta funcionalidad concreta (US-04) es la **puerta de entrada de datos** de
todo el MVP: sin un cliente registrado con su fecha de vencimiento, no puede
existir seguimiento automático de vencimientos, alertas ni tablero visual.

## Trazabilidad del pipeline (Unidad 1 → 2 → 3)

| Etapa | Agente / Unidad | Repositorio | Qué aportó a este proyecto |
|---|---|---|---|
| 1 | Discovery Agent (Unidad 1) | [`Discovery-Agents`](https://github.com/Jumandi08/Discovery-Agents) | MVP Canvas "Gestor de vencimientos de firmas digitales" (`discoveries/facturacion/outputs/mvp-canvas.md`), con la funcionalidad mínima 1 que origina esta historia |
| 2 | Agile Delivery Team (Unidad 2) | [`agile-delivery-team`](https://github.com/Jumandi08/agile-delivery-team) | Épica **E-02 "Cartera con dato de vencimiento confiable"** e historia **US-04** (3 puntos, sin dependencias), ya validada con el gate DoR/INVEST (`deliveries/facturacion/outputs/stories.md`) |
| 3 | **Este repositorio** | `facturacion-firmas-service` | Implementación de US-04 con Spec-Kit + TDD (Java/Spring Boot) |
| 4 | Quality Agent (Unidad 3) | [`quality-agent-facturacion`](https://github.com/Jumandi08/quality-agent-facturacion) | Auditoría de Pruebas, Seguridad y Criterios de aceptación contra `specs/001-registrar-cliente-vencimiento/spec.md`, con evidencia publicada en [`quality-output/verification.json`](quality-output/verification.json) |

Ninguna de las historias, épicas o el MVP Canvas fue rehecha para este
proyecto: se tomó tal cual el resultado ya generado y validado en las
Unidades 1 y 2, y el único trabajo nuevo es la implementación SDD (Unidad
"nueva") y su verificación con el Quality Agent existente (Unidad 3).

## Historia implementada: US-04

> **Como** Proveedor/Gestor de firmas digitales,
> **quiero** registrar la fecha de vencimiento de la firma de un cliente
> nuevo,
> **para que** el sistema lo incluya en el seguimiento automático de
> vencimientos desde el primer día.

- **Épica**: E-02 "Cartera con dato de vencimiento confiable"
- **Tamaño**: 3 puntos de historia
- **Dependencias**: ninguna (por eso fue elegida como primer incremento
  demostrable del MVP)
- **Fuera de alcance explícito** (pertenece a otras historias del backlog):
  envío de alertas automáticas (US-02/US-03), tablero visual completo
  (US-01), edición/eliminación de clientes, autenticación/autorización de
  usuarios.

El detalle completo de criterios de aceptación en formato Gherkin, reglas de
negocio y edge cases está formalizado como especificación ejecutable en
[`specs/001-registrar-cliente-vencimiento/spec.md`](specs/001-registrar-cliente-vencimiento/spec.md).

## Stack y arquitectura

- **Lenguaje/runtime**: Java 21
- **Framework**: Spring Boot 3.5.3 (Web, Data JPA, Validation)
- **Build**: Gradle (wrapper incluido, `./gradlew`)
- **Persistencia**: H2 en memoria (`ddl-auto=create-drop`), pensada para esta
  etapa del MVP — sin migraciones Flyway/Liquibase por ser innecesario en
  este alcance
- **Testing**: JUnit 5, Mockito, MockMvc (`@SpringBootTest`), JaCoCo
- **Arquitectura**: hexagonal simple, en 4 paquetes:

```
domain/            entidad Cliente + lógica de cálculo de estado (sin
                    dependencias de Spring ni de persistencia)
application/       puertos (interfaces) y casos de uso
  ├─ port/          ClienteRepository (puerto de salida)
  └─ usecase/        RegistrarClienteService, ConsultarCarteraService
adapters/          adaptadores de entrada/salida
  ├─ rest/          ClienteController, ClienteMapper, DTOs
  └─ persistence/   ClienteEntity, ClienteJpaRepository, ClienteRepositoryAdapter
infrastructure/    configuración transversal
  └─ config/         GlobalExceptionHandler (@RestControllerAdvice)
```

El `domain` no conoce Spring ni JPA: `Cliente` es una clase Java pura con sus
propias reglas de validación e invariantes, y el cálculo del estado
(`AL_DIA` / `POR_VENCER` / `VENCIDA`) vive en el propio dominio
(`Cliente.calcularEstado(...)`), no en el controlador ni en la capa de
persistencia.

## Estructura del repositorio

```
facturacion-firmas-service/
├── .specify/                  configuración y memoria de Spec-Kit
│   └── memory/constitution.md  5 principios no negociables del proyecto
├── specs/
│   └── 001-registrar-cliente-vencimiento/
│       ├── spec.md            especificación funcional (qué, no cómo)
│       ├── plan.md            plan técnico (stack, arquitectura, fases)
│       ├── research.md        decisiones técnicas y alternativas evaluadas
│       ├── data-model.md      entidades y reglas de validación
│       ├── contracts/
│       │   └── clientes-api.yml   contrato OpenAPI del endpoint
│       ├── quickstart.md      guía de validación manual (curl)
│       └── tasks.md           23 tareas TDD ordenadas por fases/historia
├── src/main/java/org/ups/facturacionfirmas/
│   ├── domain/                 Cliente, EstadoCliente
│   ├── application/            puertos y casos de uso
│   ├── adapters/                REST y persistencia JPA
│   └── infrastructure/config/  manejo global de excepciones
├── src/test/java/org/ups/facturacionfirmas/
│   ├── unit/                    dominio y casos de uso (Mockito)
│   ├── integration/persistence/ adaptador JPA contra H2 real
│   └── functional/              ClienteFunctionalTest (MockMvc, end-to-end HTTP)
├── quality-output/
│   └── verification.json      evidencia versionada de la corrida APROBADA
│                               del Quality Agent (Unidad 3)
├── build.gradle                incluye el gate de cobertura JaCoCo (80%)
└── README.md
```

## Modelo de dominio

**Entidad `Cliente`** (`domain/Cliente.java`):

| Atributo | Tipo | Regla |
|---|---|---|
| `id` | `UUID` | generado automáticamente, único por cliente |
| `nombre` | `String` | obligatorio, no vacío ni solo espacios (`IllegalArgumentException` si se viola) |
| `fechaVencimiento` | `LocalDate` | obligatoria; se permite una fecha ya pasada (carga de cartera vencida) |

**Estado calculado `EstadoCliente`** (no se persiste, se deriva en cada
consulta con `Cliente.calcularEstado(hoy, N)`):

| Estado | Condición |
|---|---|
| `AL_DIA` | faltan más de N días para el vencimiento |
| `POR_VENCER` | faltan N días o menos y la fecha aún no llegó |
| `VENCIDA` | la fecha de vencimiento ya pasó **o es hoy mismo** |

El parámetro **N** (días de anticipación) es una constante de configuración
del sistema (`app.vencimiento.dias-anticipacion-n`, por defecto **15**,
mínimo permitido **7** — impuesto en el propio constructor de
`RegistrarClienteService`, que falla rápido si se configura un valor menor),
nunca un dato que se ingrese por cliente.

## API REST

Base path: `/api/v1/clientes` (`ClienteController`).

### `POST /api/v1/clientes` — registrar un cliente nuevo

Request:

```json
{
  "nombre": "Comercial Andina S.A.",
  "fechaVencimiento": "2026-08-15"
}
```

Response `201 Created`:

```json
{
  "id": "3f2c1a10-...",
  "nombre": "Comercial Andina S.A.",
  "fechaVencimiento": "2026-08-15",
  "estado": "POR_VENCER"
}
```

Response `400 Bad Request` (nombre o fecha faltante/ inválida), manejado de
forma centralizada por `GlobalExceptionHandler`:

```json
{
  "campo": "fechaVencimiento",
  "mensaje": "fechaVencimiento es obligatoria"
}
```

### `GET /api/v1/clientes` — consultar la cartera completa

Response `200 OK` (cartera vacía, nunca error):

```json
[]
```

Response `200 OK` (con clientes registrados):

```json
[
  { "id": "...", "nombre": "Comercial Andina S.A.", "fechaVencimiento": "2026-08-15", "estado": "POR_VENCER" },
  { "id": "...", "nombre": "Cliente Legado",         "fechaVencimiento": "2020-01-01", "estado": "VENCIDA" }
]
```

El contrato formal (OpenAPI) está versionado en
[`specs/001-registrar-cliente-vencimiento/contracts/clientes-api.yml`](specs/001-registrar-cliente-vencimiento/contracts/clientes-api.yml).
Los 4 escenarios manuales con `curl` (alta válida, rechazo sin fecha,
cartera vacía, cliente con fecha ya vencida) están documentados paso a paso
en [`quickstart.md`](specs/001-registrar-cliente-vencimiento/quickstart.md).

## Artefactos de Spec-Kit (SDD)

Este proyecto sigue el flujo estándar de Spec-Kit, en orden:

1. `/speckit-constitution` → [`​.specify/memory/constitution.md`](.specify/memory/constitution.md)
   (5 principios: Test-First, Cobertura mínima verificable ≥80%, Seguridad
   por defecto, Trazabilidad Spec→Código→Prueba, Simplicidad/YAGNI)
2. `/speckit-specify` → [`spec.md`](specs/001-registrar-cliente-vencimiento/spec.md)
   (3 historias de usuario priorizadas, 8 Functional Requirements FR-001 a
   FR-008, 5 edge cases, 4 criterios de éxito medibles)
3. `/speckit-plan` → [`plan.md`](specs/001-registrar-cliente-vencimiento/plan.md),
   [`research.md`](specs/001-registrar-cliente-vencimiento/research.md),
   [`data-model.md`](specs/001-registrar-cliente-vencimiento/data-model.md),
   [`contracts/clientes-api.yml`](specs/001-registrar-cliente-vencimiento/contracts/clientes-api.yml),
   [`quickstart.md`](specs/001-registrar-cliente-vencimiento/quickstart.md)
4. `/speckit-tasks` → [`tasks.md`](specs/001-registrar-cliente-vencimiento/tasks.md)
   (23 tareas ordenadas TDD, organizadas en 6 fases: Setup, Fundacional,
   User Story 1/2/3 y Polish)
5. Implementación manual TDD siguiendo `tasks.md`, tarea por tarea, con test
   escrito antes o junto al código de producción (Principio I de la
   constitución, no negociable)

## Cómo ejecutar el servicio

```bash
./gradlew bootRun
```

El servicio queda disponible en `http://localhost:8080`. Si el puerto 8080
está ocupado:

```bash
./gradlew bootRun --args='--server.port=8099'
```

## Cómo ejecutar las pruebas y la cobertura

```bash
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification
```

Resultado real de la última corrida verificada (commit `04c9120`):

- **25/25 pruebas pasando** (unitarias de dominio y casos de uso con
  Mockito, integración del adaptador JPA contra H2 real, y funcionales
  end-to-end con MockMvc)
- **95.74% de cobertura de línea global** (90 de 94 líneas; las 4 líneas sin
  cubrir corresponden a `equals()`/`hashCode()` de `Cliente.java`, no a
  lógica de negocio)
- Umbral mínimo verificado automáticamente por Gradle: **80% global y por
  clase**, con el `counter = 'LINE'` de JaCoCo configurado explícitamente en
  `build.gradle` (`jacocoTestCoverageVerification`) — si baja del umbral, el
  build falla.
- Reporte HTML navegable en `build/reports/jacoco/test/html/index.html`
  tras ejecutar el comando anterior.

## Quality Gate (Unidad 3)

Este servicio fue auditado dos veces por el **Quality Agent real de la
Unidad 3** (no un agente nuevo creado para este proyecto), ejecutado desde
[`quality-agent-facturacion`](https://github.com/Jumandi08/quality-agent-facturacion)
apuntando a este repositorio, contra los tres pilares (Pruebas, Seguridad,
Criterios de aceptación) definidos en `specs/001-registrar-cliente-vencimiento/spec.md`:

| Corrida | Commit | Veredicto | Hallazgo clave |
|---|---|---|---|
| 1 | `04c9120` (antes del fix) | 🔴 **BLOQUEADO** | FR-005 no estaba validado en código: el mínimo de 7 días para el parámetro N no se hacía cumplir |
| 2 | `04c9120` (con el fix aplicado) | 🟢 **APROBADO** | Los 8 Functional Requirements (FR-001 a FR-008) cumplen, con evidencia línea por línea |

El fix real aplicado tras el primer bloqueo: `RegistrarClienteService` ahora
lanza `IllegalArgumentException` en su constructor si
`app.vencimiento.dias-anticipacion-n` es menor a 7 (commit `04c9120`), con
dos pruebas nuevas que cubren el límite (`N=6` rechazado, `N=7` aceptado).

La evidencia completa de la corrida aprobada — resultado de tests, cobertura,
resultado de seguridad (Semgrep MCP + revisión de secretos), y trazabilidad
FR-001..FR-008 → archivo:línea de prueba — está versionada en
[`quality-output/verification.json`](quality-output/verification.json).

Resumen de esa evidencia:

- **Pruebas**: 25/25 passed, 95.74% cobertura de línea
- **Seguridad**: 0 críticos, 0 altos, 0 secretos detectados (Semgrep MCP
  sobre los 14 archivos de `src/main/java` + revisión manual). Único hallazgo
  informativo: los endpoints no tienen autenticación — deuda **ya declarada
  explícitamente** en `spec.md` (sección Assumptions y FR-008), por lo que
  el Quality Agent lo registra como deuda conocida y no como hallazgo nuevo
- **Criterios de aceptación**: los 8 FR (FR-001 a FR-008) están marcados
  `"cumple"`, cada uno con la ruta y el nombre exacto del test que lo prueba

## Decisiones y deuda técnica declarada

- **Sin autenticación en esta funcionalidad**: decisión de alcance
  explícita heredada de la historia US-04 y del MVP Canvas de Unidad 1, no
  un descuido. Documentada en `spec.md` (Assumptions) y confirmada como
  deuda conocida (no hallazgo nuevo) por el Quality Agent.
- **H2 en memoria en vez de una base de datos persistente**: consistente con
  esta etapa del MVP; no se requieren migraciones Flyway/Liquibase en este
  alcance.
- **N global y único para todo el sistema**: no hay un N distinto por
  cliente o por Proveedor/Gestor; queda para una iteración futura si el
  negocio lo requiere (ver `spec.md`, Assumptions).
- Ver [`07-recomendaciones`](https://github.com/Jumandi08/facturacion-firmas-service)
  del informe final del proyecto para las recomendaciones derivadas de estos
  puntos (p. ej. promover la autenticación a un ADR aceptado antes de un
  piloto real con datos de clientes reales).

## Historial de commits relevantes

```
dbb7972  chore: bootstrap Spring Boot project (Java 21, Gradle, Web/JPA/H2/Validation/Lombok)
63ecee1  chore: initialize Spec-Kit (specify) with Claude Code integration
07ecd2c  docs: add spec.md, plan.md, research.md, data-model.md, contracts and quickstart for US-04
ce2b1f1  docs: generate tasks.md for US-04 (23 TDD-ordered tasks)
5b7d396  feat: implement US-04 (registrar cliente con vencimiento de firma) with TDD
878945d  docs: add README with traceability and usage instructions
04c9120  fix: enforce FR-005 minimum of 7 days for dias-anticipacion-n
f2aba57  docs: add Quality Agent verification.json (APROBADO)
```
