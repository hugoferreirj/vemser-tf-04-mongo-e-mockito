package br.com.dbc.wbhealth.controller;

import br.com.dbc.wbhealth.documentation.RelatorioLogControllerDoc;
import br.com.dbc.wbhealth.exceptions.DataInvalidaException;
import br.com.dbc.wbhealth.model.dto.relatoriolog.RelatorioLogOutputDTO;
import br.com.dbc.wbhealth.service.RelatorioLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/relatorio-log")
public class RelatorioLogController implements RelatorioLogControllerDoc {
    private final RelatorioLogService relatorioLogService;

    @GetMapping("/group-and-count")
    public ResponseEntity<List<RelatorioLogOutputDTO>> groupByDescricaoAndCount() {
        return ResponseEntity.status(HttpStatus.OK).body(relatorioLogService.groupByDescricaoAndCount());
    }
    @GetMapping("/group-and-count-by-date")
    public ResponseEntity<List<RelatorioLogOutputDTO>> groupByDescricaoAndCountByDate(@RequestParam String data) throws DataInvalidaException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(relatorioLogService.groupByDescricaoAndCountByDate(data));
    }
}
