# Tasks: Registrar cliente con fecha de vencimiento de firma digital

**Input**: spec.md, plan.md, research.md, data-model.md, contracts/clientes-api.yml, quickstart.md
**Constitución**: Test-First es NON-NEGOTIABLE (Principio I) — cada tarea de código
de producción va acompañada de su tarea de test, y el test se escribe primero.

**Tests**: incluidas explícitamente por mandato de la constitución (TDD), no son
opcionales en este proyecto.

## Phase 1: Setup

- [X] T001 Configurar dependencias en `build.gradle`: spring-boot-starter-web,
  spring-boot-starter-data-jpa, spring-boot-starter-validation, com.h2database:h2,
  lombok, jacoco. Configurar bloque `jacocoTestReport` y umbral de cobertura 80%.
- [X] T002 [P] Configurar `src/main/resources/application.properties` con H2 en
  memoria (`spring.datasource.url=jdbc:h2:mem:facturacion`,
  `spring.jpa.hibernate.ddl-auto=create-drop`) y la propiedad
  `app.vencimiento.dias-anticipacion-n=15`.
- [X] T003 [P] Configurar `src/test/resources/application.properties` con H2 en
  memoria independiente para tests.

## Phase 2: Foundational (bloqueante para todas las historias)

- [X] T004 Crear enum `EstadoCliente` en
  `src/main/java/org/ups/facturacionfirmas/domain/EstadoCliente.java` con
  valores `AL_DIA`, `POR_VENCER`, `VENCIDA`.
- [X] T005 Escribir prueba unitaria
  `src/test/java/org/ups/facturacionfirmas/unit/domain/ClienteTest.java` para
  la entidad `Cliente`: construcción válida, rechazo de nombre en blanco,
  rechazo de fecha nula, y `calcularEstado()` para los 3 casos (al día, por
  vencer, vencida) incluyendo el borde "vence hoy" → `VENCIDA`.
- [X] T006 Implementar entidad de dominio `Cliente` en
  `src/main/java/org/ups/facturacionfirmas/domain/Cliente.java`: campos `id`,
  `nombre`, `fechaVencimiento`; constructor que valida invariantes (FR-002,
  FR-003); método `calcularEstado(LocalDate hoy, int diasAnticipacionN)` que
  implementa la lógica de `data-model.md`. Debe hacer pasar T005.
- [X] T007 [P] Definir el puerto `ClienteRepository` en
  `src/main/java/org/ups/facturacionfirmas/application/port/ClienteRepository.java`
  con métodos `guardar(Cliente)` y `buscarTodos()`.

**Checkpoint**: dominio y puerto listos; las historias de usuario pueden
implementarse a partir de aquí.

## Phase 3: User Story 1 - Registrar un cliente nuevo con su fecha de vencimiento (Priority: P1) 🎯 MVP

**Goal**: permitir dar de alta un cliente válido y que quede disponible en la
cartera con su estado calculado.

**Independent Test**: `POST /api/v1/clientes` con nombre y fecha válidos
devuelve `201` con id, nombre, fecha y estado.

- [X] T008 [P] [US1] Escribir prueba de integración
  `src/test/java/org/ups/facturacionfirmas/integration/persistence/ClienteJpaRepositoryTest.java`
  que persiste un `Cliente` vía el adaptador JPA y lo recupera.
- [X] T009 [P] [US1] Implementar `ClienteEntity` (JPA) en
  `src/main/java/org/ups/facturacionfirmas/adapters/persistence/ClienteEntity.java`,
  `ClienteJpaRepository` (Spring Data) y `ClienteRepositoryAdapter` (implementa
  el puerto `ClienteRepository`) en `adapters/persistence/`. Debe hacer pasar T008.
- [X] T010 [US1] Escribir prueba unitaria
  `src/test/java/org/ups/facturacionfirmas/unit/application/RegistrarClienteServiceTest.java`
  con mock de `ClienteRepository`: caso feliz (guarda y devuelve cliente con
  estado calculado usando N inyectado).
- [X] T011 [US1] Implementar `RegistrarClienteService` en
  `src/main/java/org/ups/facturacionfirmas/application/usecase/RegistrarClienteService.java`,
  inyectando `ClienteRepository` y la propiedad `dias-anticipacion-n`. Debe
  hacer pasar T010.
- [X] T012 [US1] Escribir prueba funcional
  `src/test/java/org/ups/facturacionfirmas/functional/ClienteFunctionalTest.java`
  (`@SpringBootTest` + `MockMvc`): `should_return_201_with_cliente_when_datos_validos`
  (Acceptance Scenario 1 de US1) y
  `should_return_estado_calculado_when_cliente_registrado` (Acceptance Scenario 2).
- [X] T013 [US1] Implementar `RegistrarClienteRequest`, `ClienteResponse`,
  `ClienteMapper` y el método `POST` de `ClienteController` en
  `src/main/java/org/ups/facturacionfirmas/adapters/rest/`, conforme a
  `contracts/clientes-api.yml`. Debe hacer pasar T012.

**Checkpoint**: User Story 1 completa y demostrable de forma independiente
(alta de cliente + estado calculado).

## Phase 4: User Story 2 - Rechazar el alta cuando falta la fecha de vencimiento (Priority: P2)

**Goal**: garantizar que el sistema rechaza altas con datos incompletos.

**Independent Test**: `POST /api/v1/clientes` sin `fechaVencimiento` (o sin
`nombre`) devuelve `400` con el campo identificado, y no persiste nada.

- [X] T014 [US2] Ampliar `ClienteTest.java` (T005) si hace falta cubrir casos
  límite adicionales de validación de dominio (nombre solo espacios).
- [X] T015 [US2] Escribir casos en `ClienteFunctionalTest.java`:
  `should_return_400_when_fecha_vencimiento_faltante` y
  `should_return_400_when_nombre_en_blanco` (Acceptance Scenarios 1 y 2 de US2).
- [X] T016 [US2] Añadir anotaciones `@NotBlank`/`@NotNull` en
  `RegistrarClienteRequest` e implementar
  `GlobalExceptionHandler` en
  `src/main/java/org/ups/facturacionfirmas/infrastructure/config/GlobalExceptionHandler.java`
  que traduce `MethodArgumentNotValidException` a HTTP 400 con `campo` y
  `mensaje`. Debe hacer pasar T015.

**Checkpoint**: User Story 2 completa; altas inválidas quedan bloqueadas sin
romper el flujo válido de User Story 1.

## Phase 5: User Story 3 - Consultar la cartera de clientes registrados (Priority: P3)

**Goal**: exponer el listado completo de clientes con su estado, incluyendo el
caso de cartera vacía.

**Independent Test**: `GET /api/v1/clientes` devuelve `[]` sin clientes, y la
lista completa con estados correctos tras registrar varios.

- [X] T017 [US3] Escribir prueba unitaria
  `src/test/java/org/ups/facturacionfirmas/unit/application/ConsultarCarteraServiceTest.java`
  con mock de `ClienteRepository`: lista vacía y lista con múltiples clientes
  mapeados a su estado.
- [X] T018 [US3] Implementar `ConsultarCarteraService` en
  `src/main/java/org/ups/facturacionfirmas/application/usecase/ConsultarCarteraService.java`.
  Debe hacer pasar T017.
- [X] T019 [US3] Añadir casos a `ClienteFunctionalTest.java`:
  `should_return_200_with_empty_list_when_no_clientes` y
  `should_return_200_with_clientes_and_estados_when_multiple_registered`
  (Acceptance Scenarios 1 y 2 de US3).
- [X] T020 [US3] Implementar el método `GET` de `ClienteController` usando
  `ConsultarCarteraService`. Debe hacer pasar T019.

**Checkpoint**: las 3 historias de usuario completas e independientemente
demostrables.

## Phase 6: Polish & Cross-Cutting

- [X] T021 [P] Ejecutar `./gradlew clean test jacocoTestReport` y verificar
  cobertura global y por clase ≥ 80% (Principio II de la constitución). Ajustar
  pruebas si alguna clase queda por debajo.
- [X] T022 [P] Revisar `quickstart.md` ejecutando manualmente los 4 escenarios
  con `curl` contra `./gradlew bootRun`, confirmando que coinciden con el
  comportamiento real.
- [X] T023 Crear carpeta `quality-output/` (vacía, `.gitkeep`) para que el
  Quality Agent externo (Unidad 3) escriba ahí su `verification.json`.

## Dependencies & Execution Order

- **Setup (Phase 1)** → **Foundational (Phase 2)**: bloquean todo lo demás.
- **User Story 1 (Phase 3)**: depende solo de Foundational. Es el MVP.
- **User Story 2 (Phase 4)**: depende de Foundational y de que exista
  `ClienteController`/`RegistrarClienteRequest` (T013 de US1), porque añade
  validación sobre el mismo endpoint.
- **User Story 3 (Phase 5)**: depende de Foundational; puede implementarse en
  paralelo a US2 (usa otro método del controller), pero requiere T009 (adaptador
  de persistencia) de US1.
- **Polish (Phase 6)**: depende de que las 3 historias estén completas.

## Parallel Execution Examples

Dentro de Phase 2 (Foundational): T005 y T007 pueden hacerse en paralelo (no
comparten archivo). T004 debe ir antes de T005/T006 (el enum es dependencia del
test y de la entidad).

Dentro de Phase 3 (US1): T008 y T010 pueden escribirse en paralelo (archivos de
test distintos); T009 y T011 dependen de sus respectivos tests (T008, T010).

## Implementation Strategy (MVP primero)

1. Completar Phase 1 + Phase 2 (Setup + Foundational).
2. Completar Phase 3 (User Story 1) → **MVP demostrable**: alta de cliente con
   estado calculado.
3. Completar Phase 4 (User Story 2) → incremento: validación robusta.
4. Completar Phase 5 (User Story 3) → incremento: visibilidad de cartera.
5. Completar Phase 6 (Polish) → cobertura verificada y evidencia lista para el
   Quality Agent.
