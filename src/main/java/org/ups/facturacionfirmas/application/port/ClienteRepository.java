package org.ups.facturacionfirmas.application.port;

import org.ups.facturacionfirmas.domain.Cliente;

import java.util.List;

public interface ClienteRepository {

    Cliente guardar(Cliente cliente);

    List<Cliente> buscarTodos();
}
