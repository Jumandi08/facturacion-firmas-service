<!--
Sync Impact Report
Version change: (none) → 1.0.0
Modified principles: n/a (initial ratification)
Added sections: Core Principles (I-V), Restricciones Técnicas, Flujo de Calidad, Governance
Removed sections: none
Templates requiring updates:
  ✅ .specify/templates/plan-template.md (no changes needed, generic Constitution Check gate applies)
  ✅ .specify/templates/spec-template.md (no changes needed)
  ✅ .specify/templates/tasks-template.md (no changes needed)
Follow-up TODOs: none
-->
# facturacion-firmas-service Constitution

## Core Principles

### I. Test-First (NON-NEGOTIABLE)
Toda funcionalidad nueva se construye con TDD: se escribe la prueba (unitaria,
de integración o funcional) antes o junto con el código de producción que la
satisface; nunca después como ocurrencia tardía. Ciclo Red-Green-Refactor
obligatorio. Un Pull Request sin pruebas para el comportamiento que introduce
no está listo para revisión.
**Razón:** el Quality Agent de la Unidad 3 audita el código ya construido, no
las intenciones; sin pruebas no hay evidencia verificable de que el
comportamiento existe ni de que se mantendrá con refactors futuros.

### II. Cobertura Mínima Verificable (NON-NEGOTIABLE)
Cobertura de línea (JaCoCo) ≥ 80% tanto a nivel global del proyecto como por
clase individual con líneas ejecutables. Se mide con
`./gradlew clean test jacocoTestReport` y se lee del XML generado, nunca se
estima a ojo.
**Razón:** es el mismo umbral que el Quality Agent externo (Unidad 3) exige
para dar el veredicto APROBADO; fijar el mismo número aquí evita sorpresas en
el gate.

### III. Seguridad por Defecto
Cero vulnerabilidades críticas o "high" sin mitigar antes de considerar una
funcionalidad terminada. Cero secretos hardcodeados en código o configuración
versionada (usar variables de entorno). Todo endpoint HTTP que exponga datos
de negocio requiere autenticación explícita salvo que el spec lo declare
público de forma justificada.
**Razón:** el pilar de Seguridad del Quality Agent (Semgrep + revisión manual)
bloquea el gate ante cualquier hallazgo crítico/high no resuelto.

### IV. Trazabilidad Spec → Código → Prueba
Cada Functional Requirement (`FR-xxx`) del `spec.md` de la funcionalidad debe
tener al menos una prueba automatizada que lo demuestre, referenciada por
nombre de clase y método. Ningún criterio de aceptación se marca "cumple" sin
esa evidencia. Los casos de error y edge cases del spec son tan obligatorios
de probar como el camino feliz.
**Razón:** el Quality Agent cruza cada `FR-xxx` del spec contra la prueba que
lo cubre; un requisito sin prueba es, por definición, "incumple" para el gate.

### V. Simplicidad y Alcance Acotado (YAGNI)
No se construye nada fuera del alcance declarado en el `spec.md` de la
funcionalidad activa. Arquitectura hexagonal simple (domain / application /
adapters / infrastructure) sin capas ni abstracciones especulativas para
necesidades futuras no confirmadas.
**Razón:** el MVP Canvas y las historias de la Unidad 2 ya delimitaron el
alcance con criterio de valor de negocio; expandirlo sin evidencia es
desperdicio y aumenta la superficie que el Quality Agent debe auditar.

## Restricciones Técnicas

Stack obligatorio: Java 21 + Spring Boot 3.5 + Gradle (wrapper `./gradlew`).
Persistencia con Spring Data JPA; H2 en memoria para desarrollo y pruebas.
Los artefactos de Spec-Kit (`spec.md`, `plan.md`, `tasks.md`) viven versionados
bajo `specs/<funcionalidad>/` y son la única fuente de verdad de requisitos
para esa funcionalidad — el código no "reinterpreta" el spec, lo implementa.

## Flujo de Calidad

El proyecto es auditado por el **Quality Agent** externo desarrollado en la
Unidad 3 del curso (repositorio separado `quality-agent-citassalud`), que:
1. Corre `./gradlew clean test jacocoTestReport` y lee el reporte JaCoCo.
2. Escanea `src/main/java` con Semgrep MCP y revisión manual de seguridad.
3. Cruza cada `FR-xxx` de `specs/<funcionalidad>/spec.md` contra las pruebas.
4. Escribe `quality-output/verification.json` y dispara un gate determinista:
   los tres pilares (Pruebas, Seguridad, Criterios) deben pasar para
   `APROBADO`; si alguno falla, el resultado es `BLOQUEADO` con el motivo.
Este repositorio no declara una funcionalidad terminada hasta que ese gate
externo confirme `APROBADO`.

## Governance

Esta constitución prevalece sobre cualquier práctica ad-hoc del equipo. Toda
enmienda requiere: (a) documentar el cambio y su razón, (b) actualizar el
número de versión según semver (MAJOR: incompatible/remueve principios;
MINOR: agrega o expande principios; PATCH: aclaraciones sin cambio de fondo),
(c) revisar que `plan-template.md`, `spec-template.md` y `tasks-template.md`
sigan siendo consistentes con los principios vigentes. El cumplimiento de los
cinco principios se revisa en cada Pull Request y, de forma determinista, en
cada corrida del Quality Agent antes de cerrar una funcionalidad como Done.

**Version**: 1.0.0 | **Ratified**: 2026-07-05 | **Last Amended**: 2026-07-05
