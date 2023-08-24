package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.dto.log.LogOutputDTO;
import br.com.dbc.wbhealth.model.entity.LogEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<LogOutputDTO> findAll() {
        Sort sortByDateDesc = Sort.by(Sort.Direction.DESC, "dataHora");

        return logRepository.findAll(sortByDateDesc).stream().map(log -> objectMapper.convertValue(log, LogOutputDTO.class)).collect(Collectors.toList());
    }
}
