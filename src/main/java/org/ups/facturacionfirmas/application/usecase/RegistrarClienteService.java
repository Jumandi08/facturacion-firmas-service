package org.ups.facturacionfirmas.application.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ups.facturacionfirmas.application.port.ClienteRepository;
import org.ups.facturacionfirmas.domain.Cliente;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class RegistrarClienteService {

    public static final int MINIMO_DIAS_ANTICIPACION_N = 7;

    private final ClienteRepository clienteRepository;
    private final int diasAnticipacionN;

    public RegistrarClienteService(
            ClienteRepository clienteRepository,
            @Value("${app.vencimiento.dias-anticipacion-n}") int diasAnticipacionN) {
        if (diasAnticipacionN < MINIMO_DIAS_ANTICIPACION_N) {
            throw new IllegalArgumentException(
                    "app.vencimiento.dias-anticipacion-n debe ser >= " + MINIMO_DIAS_ANTICIPACION_N
                            + " pero fue " + diasAnticipacionN);
        }
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
