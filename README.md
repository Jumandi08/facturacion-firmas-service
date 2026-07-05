# facturacion-firmas-service

Funcionalidad **US-04 · Registrar cliente con fecha de vencimiento de firma
digital**, implementada con **Spec-Driven Development** usando
[Spec-Kit](https://github.com/github/spec-kit), como parte del Proyecto Final
(Autonomous Software Factory) de Ingeniería de Software — Maestría en Software.

## Origen (trazabilidad)

- **Unidad 1 — Discovery Agent**: MVP Canvas "Gestor de vencimientos de firmas
  digitales" (`discoveries/facturacion`).
- **Unidad 2 — Agile Delivery Team**: historia **US-04**, épica **E-02**
  "Cartera con dato de vencimiento confiable" (`deliveries/facturacion`),
  3 puntos, formato INVEST validado por el gate DoR/INVEST.
- **Unidad 3 — Quality Agent**: este repositorio es auditado por el Quality
  Agent externo (`quality-agent-citassalud`), que valida Pruebas, Seguridad y
  Criterios de aceptación contra `specs/001-registrar-cliente-vencimiento/spec.md`.

## Stack

Java 21 + Spring Boot 3.5 + Gradle. Arquitectura hexagonal: `domain` /
`application` / `adapters` (rest, persistence) / `infrastructure`. Persistencia
H2 en memoria. Tests con JUnit 5, Mockito, MockMvc y JaCoCo (umbral 80%).

## Artefactos de Spec-Kit

Ver [`specs/001-registrar-cliente-vencimiento/`](specs/001-registrar-cliente-vencimiento):
`spec.md`, `plan.md`, `research.md`, `data-model.md`, `contracts/`,
`quickstart.md` y `tasks.md`.

## Ejecutar

```bash
./gradlew bootRun
```

Ver [`quickstart.md`](specs/001-registrar-cliente-vencimiento/quickstart.md)
para los escenarios de prueba manual con `curl`.

## Ejecutar la suite de pruebas y cobertura

```bash
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification
```

22 pruebas (unitarias, de integración y funcionales), cobertura de línea >90%
global, umbral mínimo verificado 80% global y por clase.

## Quality Gate

La evidencia de la ejecución del Quality Agent (Unidad 3) contra esta
funcionalidad se publica en `quality-output/verification.json` tras correrlo
desde el repositorio `quality-agent-citassalud` apuntando a este proyecto.
