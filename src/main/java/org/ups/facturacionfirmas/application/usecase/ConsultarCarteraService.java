package org.ups.facturacionfirmas.application.usecase;

import org.springframework.stereotype.Service;
import org.ups.facturacionfirmas.application.port.ClienteRepository;
import org.ups.facturacionfirmas.domain.Cliente;

import java.util.List;

@Service
public class ConsultarCarteraService {

    private final ClienteRepository clienteRepository;

    public ConsultarCarteraService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> consultarTodos() {
        return clienteRepository.buscarTodos();
    }
}
