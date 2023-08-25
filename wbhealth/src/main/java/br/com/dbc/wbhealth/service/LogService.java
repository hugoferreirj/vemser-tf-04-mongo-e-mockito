package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.dto.log.LogOutputDTO;
import br.com.dbc.wbhealth.model.entity.LogEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    public void create(Descricao descricao, Integer idUsuario) {
        LogEntity entity = new LogEntity();
        entity.setIdUsuario(idUsuario);
        entity.setDataHora(LocalDateTime.now());
        entity.setDescricao(descricao);
        logRepository.save(entity);
    }

    public Page<LogOutputDTO> findAll(Integer page, Integer quantidadesLog) {
        Pageable pageable = PageRequest.of(page, quantidadesLog, Sort.Direction.DESC, "dataHora");
        Page<LogEntity> logPage = logRepository.findAll(pageable);
        return logPage.map(log -> objectMapper.convertValue(log, LogOutputDTO.class));

    }
}
