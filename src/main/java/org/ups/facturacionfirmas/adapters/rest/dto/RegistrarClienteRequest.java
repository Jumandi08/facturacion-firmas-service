package org.ups.facturacionfirmas.adapters.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistrarClienteRequest(
        @NotBlank(message = "el nombre es obligatorio") String nombre,
        @NotNull(message = "la fecha de vencimiento es obligatoria") LocalDate fechaVencimiento) {
}
