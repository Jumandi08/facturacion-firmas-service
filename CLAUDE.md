# facturacion-firmas-service

Servicio Java + Spring Boot que implementa, con Spec-Driven Development
(Spec-Kit), la historia de usuario **US-04** del MVP "Gestor de vencimientos de
firmas digitales" (Proyecto Final, Unidad 3 — Ingeniería de Software).

<!-- SPECKIT START -->
Plan activo: `specs/001-registrar-cliente-vencimiento/plan.md`
<!-- SPECKIT END -->

Para el contexto completo de la funcionalidad activa, lee
`specs/001-registrar-cliente-vencimiento/spec.md` y `plan.md`.

Este repositorio es validado por el **Quality Agent** de la Unidad 3
(repositorio separado `quality-agent-citassalud`), que lee
`specs/<funcionalidad>/spec.md` y corre `./gradlew test jacocoTestReport` +
Semgrep contra este código.
