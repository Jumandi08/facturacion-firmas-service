package org.ups.facturacionfirmas.application.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ups.facturacionfirmas.application.port.ClienteRepository;
import org.ups.facturacionfirmas.domain.Cliente;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class RegistrarClienteService {

    private final ClienteRepository clienteRepository;
    private final int diasAnticipacionN;

    public RegistrarClienteService(
            ClienteRepository clienteRepository,
            @Value("${app.vencimiento.dias-anticipacion-n}") int diasAnticipacionN) {
        this.clienteRepository = clienteRepository;
        this.diasAnticipacionN = diasAnticipacionN;
    }

    public Cliente registrar(String nombre, LocalDate fechaVencimiento) {
        Cliente cliente = new Cliente(UUID.randomUUID(), nombre, fechaVencimiento);
        return clienteRepository.guardar(cliente);
    }

    public int getDiasAnticipacionN() {
        return diasAnticipacionN;
    }
}
