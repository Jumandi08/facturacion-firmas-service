package org.ups.facturacionfirmas.adapters.rest.dto;

import org.ups.facturacionfirmas.domain.EstadoCliente;

import java.time.LocalDate;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nombre,
        LocalDate fechaVencimiento,
        EstadoCliente estado) {
}
