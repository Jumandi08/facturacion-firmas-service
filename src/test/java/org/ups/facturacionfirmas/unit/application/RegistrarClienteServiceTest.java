package org.ups.facturacionfirmas.unit.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ups.facturacionfirmas.application.port.ClienteRepository;
import org.ups.facturacionfirmas.application.usecase.RegistrarClienteService;
import org.ups.facturacionfirmas.domain.Cliente;
import org.ups.facturacionfirmas.domain.EstadoCliente;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarClienteServiceTest {

    private static final int N = 15;

    @Mock
    private ClienteRepository clienteRepository;

    private RegistrarClienteService service;

    @BeforeEach
    void setUp() {
        service = new RegistrarClienteService(clienteRepository, N);
    }

    @Test
    void should_guardar_cliente_and_return_it_with_estado_calculado() {
        LocalDate fechaVencimiento = LocalDate.now().plusDays(30);
        when(clienteRepository.guardar(any(Cliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = service.registrar("Comercial Andina S.A.", fechaVencimiento);

        assertThat(resultado.getNombre()).isEqualTo("Comercial Andina S.A.");
        assertThat(resultado.getFechaVencimiento()).isEqualTo(fechaVencimiento);
        assertThat(resultado.calcularEstado(LocalDate.now(), N)).isEqualTo(EstadoCliente.AL_DIA);

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).guardar(captor.capture());
        assertThat(captor.getValue().getId()).isNotNull();
    }

    @Test
    void should_assign_unique_id_to_new_cliente() {
        when(clienteRepository.guardar(any(Cliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cliente c1 = service.registrar("Cliente A", LocalDate.now().plusDays(5));
        Cliente c2 = service.registrar("Cliente B", LocalDate.now().plusDays(5));

        assertThat(c1.getId()).isNotEqualTo(c2.getId());
    }
}
