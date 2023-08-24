package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.dto.relatoriolog.RelatorioLogOutputDTO;
import br.com.dbc.wbhealth.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioLogService {
    private final LogRepository logRepository;

    //    public List<RelatorioLogOutputDTO> groupByTipoLogAndCount() {
//        return logRepository.groupByDescricaoAndCount().stream().map(log -> {
//            return new RelatorioLogOutputDTO(log.getDescricao(), log.getQuantidade());
//        }).collect(Collectors.toList());
//    }
    public List<RelatorioLogOutputDTO> groupByTipoLogAndCount() {
        return logRepository.groupByDescricaoAndCount().stream().map(log -> {
            System.out.println("Valor de descricao: " + log.getDescricao()); // Adicione esta linha
            return new RelatorioLogOutputDTO(log.getDescricao(), log.getQuantidade());
        }).collect(Collectors.toList());
    }

}
