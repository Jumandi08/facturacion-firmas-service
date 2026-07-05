# Implementation Plan: Registrar cliente con fecha de vencimiento de firma digital

**Branch**: `001-registrar-cliente-vencimiento` | **Date**: 2026-07-05 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-registrar-cliente-vencimiento/spec.md`

## Summary

Registrar clientes con nombre y fecha de vencimiento de firma digital, calcular su
estado (`AL_DIA` / `POR_VENCER` / `VENCIDA`) a partir de un parámetro N configurable,
y exponer alta y consulta vía API REST. Arquitectura hexagonal simple sobre Spring
Boot: dominio con la lógica de cálculo de estado, casos de uso de aplicación,
adaptador REST de entrada y adaptador JPA/H2 de persistencia.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 3.5 (spring-boot-starter-web,
spring-boot-starter-data-jpa, spring-boot-starter-validation), Lombok, H2 Database

**Storage**: H2 en memoria (esquema autogenerado por Hibernate/JPA;
`ddl-auto=create-drop` para dev/test, suficiente para el alcance del MVP)

**Testing**: JUnit 5 + Mockito (unitarias de dominio y aplicación),
Spring Boot Test + MockMvc (funcionales de los endpoints REST), JaCoCo
(cobertura, umbral 80% global y por clase según la constitución)

**Target Platform**: JVM 21, servicio HTTP standalone (Spring Boot embedded Tomcat)

**Project Type**: web-service (API REST única, sin frontend)

**Performance Goals**: consulta de cartera responde en <1s con hasta 500 clientes
(SC-004 del spec); sin requisitos de throughput alto en este MVP

**Constraints**: sin autenticación en esta funcionalidad (ver Assumptions del
spec); sin envío de notificaciones (fuera de alcance, FR-008)

**Scale/Scope**: cartera individual de un Proveedor/Gestor, 10–100+ clientes
según el MVP Canvas; 3 historias de usuario (alta, validación, consulta)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principio | Cumplimiento planeado |
|---|---|
| I. Test-First | Cada caso de uso (`RegistrarClienteService`, `ConsultarCarteraService`) y cada endpoint tendrá su prueba escrita junto con el código, antes de considerarse terminado. TDD por tarea en `tasks.md`. |
| II. Cobertura ≥80% | Se mide con `./gradlew clean test jacocoTestReport`; el diseño mantiene clases pequeñas y testeables (dominio sin dependencias de framework) para facilitar cobertura real, no artificial. |
| III. Seguridad por defecto | No hay secretos en esta funcionalidad (no hay credenciales externas). La ausencia de autenticación es una decisión documentada en Assumptions del spec, no un descuido — se declara explícitamente para que el Quality Agent la evalúe con contexto. |
| IV. Trazabilidad spec→código→prueba | Cada FR-00x del spec se mapea 1:1 a al menos un test funcional o unitario nombrado explícitamente en `tasks.md`. |
| V. Simplicidad (YAGNI) | Sin capa de autenticación, sin mensajería, sin tablero visual — exactamente el alcance de US-04. Persistencia JPA directa sin CQRS ni capas adicionales. |

Gate: **PASA**. No hay violaciones que requieran justificación en Complexity Tracking.

## Project Structure

### Documentation (this feature)

```text
specs/001-registrar-cliente-vencimiento/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/
│   └── clientes-api.yml # Phase 1 output (OpenAPI)
└── tasks.md             # Phase 2 output (/speckit-tasks)
```

### Source Code (repository root)

```text
src/main/java/org/ups/facturacionfirmas/
├── FacturacionFirmasServiceApplication.java
├── domain/
│   ├── Cliente.java                     # entidad de dominio + cálculo de EstadoCliente
│   └── EstadoCliente.java               # enum AL_DIA / POR_VENCER / VENCIDA
├── application/
│   ├── port/
│   │   └── ClienteRepository.java       # puerto (interfaz) hacia persistencia
│   └── usecase/
│       ├── RegistrarClienteService.java
│       └── ConsultarCarteraService.java
├── adapters/
│   ├── rest/
│   │   ├── ClienteController.java
│   │   ├── dto/
│   │   │   ├── RegistrarClienteRequest.java
│   │   │   └── ClienteResponse.java
│   │   └── ClienteMapper.java
│   └── persistence/
│       ├── ClienteEntity.java
│       ├── ClienteJpaRepository.java    # Spring Data JPA
│       └── ClienteRepositoryAdapter.java # implementa el puerto de application
└── infrastructure/
    └── config/
        └── GlobalExceptionHandler.java  # mapea errores de validación a HTTP 400

src/test/java/org/ups/facturacionfirmas/
├── unit/domain/ClienteTest.java
├── unit/application/RegistrarClienteServiceTest.java
├── unit/application/ConsultarCarteraServiceTest.java
├── integration/persistence/ClienteJpaRepositoryTest.java
└── functional/ClienteFunctionalTest.java
```

**Structure Decision**: Opción "Single project" (web-service), arquitectura
hexagonal dentro de un único módulo Gradle. No se justifica separar en
subproyectos (frontend/backend, api/mobile): es un único servicio REST.

## Complexity Tracking

*No aplica — el Constitution Check no reportó violaciones.*
