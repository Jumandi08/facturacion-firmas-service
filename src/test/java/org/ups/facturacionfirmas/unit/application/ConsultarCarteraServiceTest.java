package org.ups.facturacionfirmas.unit.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.facturacionfirmas.application.port.ClienteRepository;
import org.ups.facturacionfirmas.application.usecase.ConsultarCarteraService;
import org.ups.facturacionfirmas.domain.Cliente;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCarteraServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Test
    void should_return_empty_list_when_no_clientes() {
        when(clienteRepository.buscarTodos()).thenReturn(List.of());
        ConsultarCarteraService service = new ConsultarCarteraService(clienteRepository);

        List<Cliente> resultado = service.consultarTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    void should_return_all_clientes_when_multiple_registered() {
        Cliente c1 = new Cliente(UUID.randomUUID(), "Cliente A", LocalDate.now().plusDays(5));
        Cliente c2 = new Cliente(UUID.randomUUID(), "Cliente B", LocalDate.now().minusDays(5));
        when(clienteRepository.buscarTodos()).thenReturn(List.of(c1, c2));
        ConsultarCarteraService service = new ConsultarCarteraService(clienteRepository);

        List<Cliente> resultado = service.consultarTodos();

        assertThat(resultado).containsExactly(c1, c2);
    }
}
