package org.ups.facturacionfirmas.functional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_201_with_cliente_when_datos_validos() throws Exception {
        String body = """
                {"nombre": "Comercial Andina S.A.", "fechaVencimiento": "%s"}
                """.formatted(java.time.LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nombre").value("Comercial Andina S.A."));
    }

    @Test
    void should_return_estado_calculado_when_cliente_registrado() throws Exception {
        String body = """
                {"nombre": "Cliente Legado", "fechaVencimiento": "2020-01-01"}
                """;

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("VENCIDA"));
    }

    @Test
    void should_return_400_when_fecha_vencimiento_faltante() throws Exception {
        String body = """
                {"nombre": "Comercial Andina S.A."}
                """;

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campo").value("fechaVencimiento"));
    }

    @Test
    void should_return_400_when_nombre_en_blanco() throws Exception {
        String body = """
                {"nombre": "   ", "fechaVencimiento": "%s"}
                """.formatted(java.time.LocalDate.now().plusDays(10));

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campo").value("nombre"));
    }

    @Test
    void should_return_200_with_empty_list_when_no_clientes() throws Exception {
        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    void should_return_200_with_clientes_and_estados_when_multiple_registered() throws Exception {
        String cliente1 = """
                {"nombre": "Cliente Al Dia", "fechaVencimiento": "%s"}
                """.formatted(java.time.LocalDate.now().plusDays(60));
        String cliente2 = """
                {"nombre": "Cliente Vencido", "fechaVencimiento": "2020-01-01"}
                """;

        mockMvc.perform(post("/api/v1/clientes").contentType(MediaType.APPLICATION_JSON).content(cliente1))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/clientes").contentType(MediaType.APPLICATION_JSON).content(cliente2))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre == 'Cliente Al Dia')].estado").value("AL_DIA"))
                .andExpect(jsonPath("$[?(@.nombre == 'Cliente Vencido')].estado").value("VENCIDA"));
    }
}
