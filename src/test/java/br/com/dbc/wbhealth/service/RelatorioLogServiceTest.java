package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.DataInvalidaException;
import br.com.dbc.wbhealth.model.dto.relatoriolog.RelatorioLogOutputDTO;
import br.com.dbc.wbhealth.model.entity.RelatorioLogEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.LogRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RelatorioLogServiceTest {
    @InjectMocks
    private RelatorioLogService relatorioLogService;

    @Mock
    private LogRepository logRepository;

    RelatorioLogEntity relatorioLogEntityLogin = new RelatorioLogEntity();
    RelatorioLogEntity relatorioLogEntityCreate = new RelatorioLogEntity();
    RelatorioLogEntity relatorioLogEntityDelete = new RelatorioLogEntity();
    RelatorioLogEntity relatorioLogEntityUpdate = new RelatorioLogEntity();

    RelatorioLogOutputDTO relatorioLogDTOLogin = new RelatorioLogOutputDTO();
    RelatorioLogOutputDTO relatorioLogDTOCreate = new RelatorioLogOutputDTO();
    RelatorioLogOutputDTO relatorioLogDTODelete = new RelatorioLogOutputDTO();
    RelatorioLogOutputDTO relatorioLogDTOUpdate = new RelatorioLogOutputDTO();


    @BeforeEach
    private void criarEntidadesDeApoio() {
        relatorioLogEntityLogin.setDescricao(Descricao.LOGIN);
        relatorioLogEntityLogin.setQuantidade(3);
        relatorioLogEntityCreate.setDescricao(Descricao.CREATE);
        relatorioLogEntityCreate.setQuantidade(4);
        relatorioLogEntityDelete.setDescricao(Descricao.DELETE);
        relatorioLogEntityDelete.setQuantidade(5);
        relatorioLogEntityUpdate.setDescricao(Descricao.UPDATE);
        relatorioLogEntityUpdate.setQuantidade(0);
        BeanUtils.copyProperties(relatorioLogEntityLogin, relatorioLogDTOLogin);
        BeanUtils.copyProperties(relatorioLogEntityCreate, relatorioLogDTOCreate);
        BeanUtils.copyProperties(relatorioLogEntityUpdate, relatorioLogDTOUpdate);
        BeanUtils.copyProperties(relatorioLogEntityDelete, relatorioLogDTODelete);
    }

    @Test
    public void testGroupByDescricaoAndCountByDate() throws DataInvalidaException {
        List<RelatorioLogEntity> relatorioLogEntityList = new ArrayList<>();
        relatorioLogEntityList.add(relatorioLogEntityLogin);
        relatorioLogEntityList.add(relatorioLogEntityDelete);
        relatorioLogEntityList.add(relatorioLogEntityUpdate);
        relatorioLogEntityList.add(relatorioLogEntityCreate);


        List<RelatorioLogOutputDTO> relatorioLogDTOList = new ArrayList<>();
        relatorioLogDTOList.add(relatorioLogDTOLogin);
        relatorioLogDTOList.add(relatorioLogDTODelete);
        relatorioLogDTOList.add(relatorioLogDTOUpdate);
        relatorioLogDTOList.add(relatorioLogDTOCreate);

        when(logRepository.countLogsByDescricaoAndData(any(), any())).thenReturn(relatorioLogEntityList);

        List<RelatorioLogOutputDTO> result = relatorioLogService.groupByDescricaoAndCountByDate("2023-08-26");

        verify(logRepository, times(1)).countLogsByDescricaoAndData(any(), any());

        // Ordena as listas para garantir a mesma ordem
        Collections.sort(result, Comparator.comparing(RelatorioLogOutputDTO::getDescricao));
        Collections.sort(relatorioLogDTOList, Comparator.comparing(RelatorioLogOutputDTO::getDescricao));

        Assertions.assertEquals(relatorioLogDTOList.size(), result.size());
        for (int i = 0; i < relatorioLogDTOList.size(); i++) {
            RelatorioLogOutputDTO expectedDTO = relatorioLogDTOList.get(i);
            RelatorioLogOutputDTO actualDTO = result.get(i);

            Assertions.assertEquals(expectedDTO.getDescricao(), actualDTO.getDescricao());
            Assertions.assertEquals(expectedDTO.getQuantidade(), actualDTO.getQuantidade());
        }
    }

    @Test
    public void testGroupByDescricaoAndCountByDateWithInvalidData() {
        Assertions.assertThrows(DataInvalidaException.class, () -> {
            relatorioLogService.groupByDescricaoAndCountByDate("Data inválida!");
        });
    }

    @Test
    public void testGroupByDescricaoAndCount() {
        List<RelatorioLogEntity> relatorioLogEntityList = new ArrayList<>();
        relatorioLogEntityList.add(relatorioLogEntityLogin);
        relatorioLogEntityList.add(relatorioLogEntityDelete);
        relatorioLogEntityList.add(relatorioLogEntityUpdate);
        relatorioLogEntityList.add(relatorioLogEntityCreate);


        List<RelatorioLogOutputDTO> relatorioLogDTOList = new ArrayList<>();
        relatorioLogDTOList.add(relatorioLogDTOLogin);
        relatorioLogDTOList.add(relatorioLogDTODelete);
        relatorioLogDTOList.add(relatorioLogDTOUpdate);
        relatorioLogDTOList.add(relatorioLogDTOCreate);

        when(logRepository.groupByDescricaoAndCount()).thenReturn(relatorioLogEntityList);

        List<RelatorioLogOutputDTO> result = relatorioLogService.groupByDescricaoAndCount();

        verify(logRepository, times(1)).groupByDescricaoAndCount();

        Assertions.assertEquals(relatorioLogDTOList.size(), result.size());
        for (int i = 0; i < relatorioLogDTOList.size(); i++) {
            RelatorioLogOutputDTO expectedDTO = relatorioLogDTOList.get(i);
            RelatorioLogOutputDTO actualDTO = result.get(i);

            Assertions.assertEquals(expectedDTO.getDescricao(), actualDTO.getDescricao());
            Assertions.assertEquals(expectedDTO.getQuantidade(), actualDTO.getQuantidade());
        }
    }
}
