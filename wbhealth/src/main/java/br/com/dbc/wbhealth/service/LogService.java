package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.entity.LogEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
