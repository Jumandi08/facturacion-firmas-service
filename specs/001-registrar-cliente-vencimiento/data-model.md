# Data Model: Registrar cliente con fecha de vencimiento de firma digital

## Entidad: Cliente

Representa a un cliente del Proveedor/Gestor cuya firma digital debe seguirse.

| Campo | Tipo | Reglas |
|---|---|---|
| `id` | `UUID` | Generado por el sistema al registrar. Único. |
| `nombre` | `String` | Obligatorio. No vacío, no solo espacios (`@NotBlank`). |
| `fechaVencimiento` | `LocalDate` | Obligatoria (`@NotNull`). Puede ser pasada, hoy o futura. |

### Estado derivado (no persistido)

`EstadoCliente` — enum calculado en tiempo de lectura, nunca almacenado:

| Valor | Condición |
|---|---|
| `VENCIDA` | `fechaVencimiento` es hoy o anterior a hoy. |
| `POR_VENCER` | `fechaVencimiento` es posterior a hoy y faltan ≤ N días. |
| `AL_DIA` | `fechaVencimiento` es posterior a hoy y faltan > N días. |

Cálculo: `diasRestantes = ChronoUnit.DAYS.between(hoy, fechaVencimiento)`.
- `diasRestantes <= 0` → `VENCIDA`
- `0 < diasRestantes <= N` → `POR_VENCER`
- `diasRestantes > N` → `AL_DIA`

### Invariantes

- Un `Cliente` nunca existe sin `fechaVencimiento` (FR-002): el constructor de
  dominio rechaza `fechaVencimiento == null`.
- Un `Cliente` nunca existe con `nombre` en blanco (FR-003): el constructor de
  dominio rechaza `nombre == null || nombre.isBlank()`.
- El `id` se asigna una única vez al crear el cliente (`UUID.randomUUID()`) y no
  cambia durante su ciclo de vida.

### Parámetro de configuración relacionado

`N` (días de anticipación) — no es un campo de `Cliente`; vive como propiedad de
configuración del sistema (`app.vencimiento.dias-anticipacion-n`, default 15,
mínimo 7) y se pasa como parámetro al método de cálculo de estado. Ver
`research.md`.

## Sin relaciones adicionales

Esta funcionalidad no introduce otras entidades: no hay `Alerta` ni `Notificacion`
en este alcance (pertenecen a US-02/US-03, fuera de alcance por FR-008).
