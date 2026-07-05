package org.ups.facturacionfirmas.integration.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.ups.facturacionfirmas.adapters.persistence.ClienteRepositoryAdapter;
import org.ups.facturacionfirmas.domain.Cliente;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ClienteRepositoryAdapter.class)
class ClienteJpaRepositoryTest {

    @Autowired
    private ClienteRepositoryAdapter clienteRepositoryAdapter;

    @Test
    void should_persist_and_retrieve_cliente() {
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente(id, "Comercial Andina S.A.", LocalDate.now().plusDays(30));

        clienteRepositoryAdapter.guardar(cliente);
        List<Cliente> todos = clienteRepositoryAdapter.buscarTodos();

        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).getId()).isEqualTo(id);
        assertThat(todos.get(0).getNombre()).isEqualTo("Comercial Andina S.A.");
    }

    @Test
    void should_return_empty_list_when_no_clientes() {
        List<Cliente> todos = clienteRepositoryAdapter.buscarTodos();

        assertThat(todos).isEmpty();
    }
}
