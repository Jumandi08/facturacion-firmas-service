package org.ups.facturacionfirmas.adapters.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ups.facturacionfirmas.adapters.rest.dto.ClienteResponse;
import org.ups.facturacionfirmas.domain.Cliente;

import java.time.LocalDate;

@Component
public class ClienteMapper {

    private final int diasAnticipacionN;

    public ClienteMapper(@Value("${app.vencimiento.dias-anticipacion-n}") int diasAnticipacionN) {
        this.diasAnticipacionN = diasAnticipacionN;
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getFechaVencimiento(),
                cliente.calcularEstado(LocalDate.now(), diasAnticipacionN));
    }
}
