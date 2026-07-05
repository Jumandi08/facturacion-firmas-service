package org.ups.facturacionfirmas.unit.domain;

import org.junit.jupiter.api.Test;
import org.ups.facturacionfirmas.domain.Cliente;
import org.ups.facturacionfirmas.domain.EstadoCliente;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClienteTest {

    private static final int N = 15;

    @Test
    void should_create_cliente_when_nombre_y_fecha_validos() {
        UUID id = UUID.randomUUID();
        LocalDate vencimiento = LocalDate.now().plusDays(30);

        Cliente cliente = new Cliente(id, "Comercial Andina S.A.", vencimiento);

        assertThat(cliente.getId()).isEqualTo(id);
        assertThat(cliente.getNombre()).isEqualTo("Comercial Andina S.A.");
        assertThat(cliente.getFechaVencimiento()).isEqualTo(vencimiento);
    }

    @Test
    void should_throw_when_nombre_is_null() {
        assertThatThrownBy(() -> new Cliente(UUID.randomUUID(), null, LocalDate.now().plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void should_throw_when_nombre_is_blank() {
        assertThatThrownBy(() -> new Cliente(UUID.randomUUID(), "   ", LocalDate.now().plusDays(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
    }

    @Test
    void should_throw_when_fecha_vencimiento_is_null() {
        assertThatThrownBy(() -> new Cliente(UUID.randomUUID(), "Cliente X", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fechaVencimiento");
    }

    @Test
    void should_return_AL_DIA_when_faltan_mas_de_N_dias() {
        Cliente cliente = new Cliente(UUID.randomUUID(), "Cliente X", LocalDate.now().plusDays(N + 1));

        EstadoCliente estado = cliente.calcularEstado(LocalDate.now(), N);

        assertThat(estado).isEqualTo(EstadoCliente.AL_DIA);
    }

    @Test
    void should_return_POR_VENCER_when_faltan_exactamente_N_dias() {
        Cliente cliente = new Cliente(UUID.randomUUID(), "Cliente X", LocalDate.now().plusDays(N));

        EstadoCliente estado = cliente.calcularEstado(LocalDate.now(), N);

        assertThat(estado).isEqualTo(EstadoCliente.POR_VENCER);
    }

    @Test
    void should_return_POR_VENCER_when_falta_un_dia() {
        Cliente cliente = new Cliente(UUID.randomUUID(), "Cliente X", LocalDate.now().plusDays(1));

        EstadoCliente estado = cliente.calcularEstado(LocalDate.now(), N);

        assertThat(estado).isEqualTo(EstadoCliente.POR_VENCER);
    }

    @Test
    void should_return_VENCIDA_when_fecha_vencimiento_es_hoy() {
        Cliente cliente = new Cliente(UUID.randomUUID(), "Cliente X", LocalDate.now());

        EstadoCliente estado = cliente.calcularEstado(LocalDate.now(), N);

        assertThat(estado).isEqualTo(EstadoCliente.VENCIDA);
    }

    @Test
    void should_return_VENCIDA_when_fecha_vencimiento_ya_paso() {
        Cliente cliente = new Cliente(UUID.randomUUID(), "Cliente Legado", LocalDate.now().minusDays(10));

        EstadoCliente estado = cliente.calcularEstado(LocalDate.now(), N);

        assertThat(estado).isEqualTo(EstadoCliente.VENCIDA);
    }
}
