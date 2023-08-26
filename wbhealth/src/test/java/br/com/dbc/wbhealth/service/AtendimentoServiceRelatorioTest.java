package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.DataInvalidaException;
import br.com.dbc.wbhealth.model.dto.relatorio.RelatorioLucro;
import br.com.dbc.wbhealth.model.enumarator.TipoDeAtendimento;
import br.com.dbc.wbhealth.repository.AtendimentoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceRelatorioTest {
    @InjectMocks
    private AtendimentoServiceRelatorio atendimentoServiceRelatorio;
    @Mock
    private AtendimentoRepository atendimentoRepository;

    private LocalDate dataAtual;
    private Pageable paginacaoSimulada;
    private Page<RelatorioLucro> relatoriosPaginadosSimulados;


    @BeforeEach
    public void setUp(){
        dataAtual = LocalDate.now();
        paginacaoSimulada = PageRequest.of(0, 5);

        List<RelatorioLucro> relatorios = new ArrayList<>();
        relatorios.add(new RelatorioLucro(TipoDeAtendimento.CONSULTA, 500.0));
        relatorios.add(new RelatorioLucro(TipoDeAtendimento.CIRURGIA, 1600.0));
        relatorios.add(new RelatorioLucro(TipoDeAtendimento.EXAME, 450.0));
        relatorios.add(new RelatorioLucro(TipoDeAtendimento.RETORNO, 800.0));

        relatoriosPaginadosSimulados = new PageImpl<>(relatorios, paginacaoSimulada, relatorios.size());
    }

    @AfterEach
    public void tearDown(){
        dataAtual = null;
        paginacaoSimulada = null;
        relatoriosPaginadosSimulados = null;
    }

    @Test
    void testGetLucroByData() throws DataInvalidaException {
        String inicio = "2023-01-01";
        LocalDate dataInicio = LocalDate.parse(inicio);

        when(atendimentoRepository.getLucroByData(dataInicio, dataAtual, paginacaoSimulada))
                .thenReturn(relatoriosPaginadosSimulados);

        Page<RelatorioLucro> resultado = atendimentoServiceRelatorio.getLucroByData(inicio, paginacaoSimulada);

        verify(atendimentoRepository).getLucroByData(dataInicio, dataAtual, paginacaoSimulada);
        assertNotNull(resultado);
        assertDoesNotThrow(() -> new DataInvalidaException("Data inválida"));
        assertEquals(relatoriosPaginadosSimulados, resultado);
    }

    @Test
    void testFindLucroAteAgora() {
        when(atendimentoRepository.getLucroAteOMomento(dataAtual, paginacaoSimulada))
                .thenReturn(relatoriosPaginadosSimulados);

        Page<RelatorioLucro> resultado = atendimentoServiceRelatorio.findLucroAteAgora(paginacaoSimulada);

        verify(atendimentoRepository).getLucroAteOMomento(dataAtual, paginacaoSimulada);
        assertNotNull(resultado);
        assertEquals(relatoriosPaginadosSimulados, resultado);
    }

    @Test
    void testGetLucroByDataThrowsDataInvalidaException() {
        String inicio = "01/01/2023";

        assertThrows(
                DataInvalidaException.class,
                () -> atendimentoServiceRelatorio.getLucroByData(inicio, paginacaoSimulada)
        );
    }

}