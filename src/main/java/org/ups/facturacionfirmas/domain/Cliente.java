package org.ups.facturacionfirmas.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class Cliente {

    private final UUID id;
    private final String nombre;
    private final LocalDate fechaVencimiento;

    public Cliente(UUID id, String nombre, LocalDate fechaVencimiento) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("nombre es obligatorio y no puede estar en blanco");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("fechaVencimiento es obligatoria");
        }
        this.id = Objects.requireNonNull(id, "id es obligatorio");
        this.nombre = nombre;
        this.fechaVencimiento = fechaVencimiento;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public EstadoCliente calcularEstado(LocalDate hoy, int diasAnticipacionN) {
        long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaVencimiento);
        if (diasRestantes <= 0) {
            return EstadoCliente.VENCIDA;
        }
        if (diasRestantes <= diasAnticipacionN) {
            return EstadoCliente.POR_VENCER;
        }
        return EstadoCliente.AL_DIA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente cliente)) {
            return false;
        }
        return id.equals(cliente.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
