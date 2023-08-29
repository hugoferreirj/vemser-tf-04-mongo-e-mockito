package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.DataInvalidaException;
import br.com.dbc.wbhealth.model.dto.relatoriolog.RelatorioLogOutputDTO;
import br.com.dbc.wbhealth.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioLogService {
    private final LogRepository logRepository;

    public List<RelatorioLogOutputDTO> groupByDescricaoAndCount() {
        return logRepository.groupByDescricaoAndCount().stream().map(log -> {
            return new RelatorioLogOutputDTO(log.getDescricao(), log.getQuantidade());
        }).collect(Collectors.toList());
    }


    public List<RelatorioLogOutputDTO> groupByDescricaoAndCountByDate(String data) throws DataInvalidaException {
        LocalDateTime dataInicial;
        LocalDateTime dataFinal;

        String strDataInicial = data + "T00:00:00.000";
        String strDataFinal = data + "T23:59:59.999";

        try {
            dataInicial = LocalDateTime.parse(strDataInicial);
            dataFinal = LocalDateTime.parse(strDataFinal);
        } catch (Exception e) {
            throw new DataInvalidaException("Data inválida!");
        }

        return logRepository.countLogsByDescricaoAndData(dataInicial,dataFinal).stream()
                .map(log -> new RelatorioLogOutputDTO(log.getDescricao(), log.getQuantidade()))
                .collect(Collectors.toList());
    }

}
