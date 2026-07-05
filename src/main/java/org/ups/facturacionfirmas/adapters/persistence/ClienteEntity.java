package org.ups.facturacionfirmas.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    protected ClienteEntity() {
        // requerido por JPA
    }

    public ClienteEntity(UUID id, String nombre, LocalDate fechaVencimiento) {
        this.id = id;
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
}
