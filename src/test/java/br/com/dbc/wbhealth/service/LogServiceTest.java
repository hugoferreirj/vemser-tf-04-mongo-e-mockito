package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.dto.log.LogOutputDTO;
import br.com.dbc.wbhealth.model.entity.LogEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.LogRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogServiceTest {
    @InjectMocks
    private LogService logService;

    @Mock
    private LogRepository logRepository;

    LogEntity logEntity1 = new LogEntity();

    LogEntity logEntity2 = new LogEntity();

    LogOutputDTO logOutputDTO1 = new LogOutputDTO();

    LogOutputDTO logOutputDTO2 = new LogOutputDTO();

    @BeforeEach
    private void criarEntidadesDeApoio() {
        logEntity1.setIdLog("1");
        logEntity1.setIdUsuario(123);
        logEntity1.setDataHora(LocalDateTime.of(2023, 6, 23, 10, 0));
        logEntity1.setDescricao(Descricao.LOGIN);

        logEntity2.setIdLog("2");
        logEntity2.setIdUsuario(456);
        logEntity2.setDataHora(LocalDateTime.of(2023, 5, 9, 14, 0));
        logEntity2.setDescricao(Descricao.CREATE);

        BeanUtils.copyProperties(logEntity1, logOutputDTO1);
        logOutputDTO1.setDescricao(logEntity1.getDescricao().name());

        BeanUtils.copyProperties(logEntity2, logOutputDTO2);
        logOutputDTO2.setDescricao(logEntity2.getDescricao().name());

    }


    @Test
    public void testCreate() {
        logService.create(Descricao.LOGIN, 123);

        ArgumentCaptor<LogEntity> logEntityCaptor = ArgumentCaptor.forClass(LogEntity.class);
        verify(logRepository, times(1)).save(logEntityCaptor.capture());

        LogEntity capturedEntity = logEntityCaptor.getValue();
        assertEquals(123, capturedEntity.getIdUsuario());
        assertEquals(Descricao.LOGIN, capturedEntity.getDescricao());
    }


    @Test
    public void testFindAll() {
        List<LogEntity> logsList = new ArrayList<>();
        logsList.add(logEntity1);
        logsList.add(logEntity2);
        Page<LogEntity> logEntityPage = new PageImpl<>(logsList);

        List<LogOutputDTO> logsDTOList = new ArrayList<>();
        logsDTOList.add(logOutputDTO1);
        logsDTOList.add(logOutputDTO2);
        Page<LogOutputDTO> logDTOPage = new PageImpl<>(logsDTOList);

        when(logRepository.findAll(any(Pageable.class))).thenReturn(logEntityPage);

        Page<LogOutputDTO> result = logService.findAll(0, 2); // Exemplo de valores de página e quantidadesLog

        verify(logRepository, times(1)).findAll(any(Pageable.class));
        assertEquals(logDTOPage.getTotalElements(), result.getTotalElements());
        Assertions.assertIterableEquals(logDTOPage, result);
    }

}
