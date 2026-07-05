# Quickstart: Registrar cliente con fecha de vencimiento de firma digital

## Prerrequisitos

- Java 21 instalado.
- Ninguna dependencia externa: el proyecto usa H2 en memoria.

## Levantar el servicio

```bash
./gradlew bootRun
```

El servicio queda disponible en `http://localhost:8080`.

## Escenario 1 — Registrar un cliente válido (User Story 1)

```bash
curl -i -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Comercial Andina S.A.", "fechaVencimiento": "2026-08-15"}'
```

**Esperado**: `201 Created` con `id`, `nombre`, `fechaVencimiento` y `estado`
(`AL_DIA` o `POR_VENCER` según cuántos días falten respecto a N=15).

## Escenario 2 — Rechazar alta sin fecha de vencimiento (User Story 2)

```bash
curl -i -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Comercial Andina S.A."}'
```

**Esperado**: `400 Bad Request` indicando que `fechaVencimiento` es obligatoria.

## Escenario 3 — Consultar cartera vacía (User Story 3)

```bash
curl -i http://localhost:8080/api/v1/clientes
```

**Esperado** (antes de registrar ningún cliente): `200 OK` con `[]`.

## Escenario 4 — Cliente con fecha ya vencida

```bash
curl -i -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Cliente Legado", "fechaVencimiento": "2020-01-01"}'
```

**Esperado**: `201 Created` con `estado: "VENCIDA"`.

## Validación automatizada

La validación formal de estos escenarios vive en las pruebas automatizadas
(`src/test/java/.../functional/ClienteFunctionalTest.java`), ejecutables con:

```bash
./gradlew clean test jacocoTestReport
```
