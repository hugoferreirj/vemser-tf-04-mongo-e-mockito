package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.dto.log.LogOutputDTO;
import br.com.dbc.wbhealth.model.entity.LogEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.LogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

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

        List<LogOutputDTO> logOutputDTOs = new ArrayList<>();
        for (LogEntity logEntity : logPage.getContent()) {
            logOutputDTOs.add(convertToOutputDTO(logEntity));
        }

        return new PageImpl<>(logOutputDTOs, pageable, logPage.getTotalElements());
    }

    public LogOutputDTO convertToOutputDTO(LogEntity entity){
        LogOutputDTO outputDTO = new LogOutputDTO();
        BeanUtils.copyProperties(entity, outputDTO);
        outputDTO.setDescricao(entity.getDescricao().name());
        return outputDTO;
    }
}
