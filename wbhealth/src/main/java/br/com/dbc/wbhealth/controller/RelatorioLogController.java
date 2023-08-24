package br.com.dbc.wbhealth.controller;

import br.com.dbc.wbhealth.model.dto.relatoriolog.RelatorioLogOutputDTO;
import br.com.dbc.wbhealth.service.RelatorioLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/relatorio-log")
public class RelatorioLogController {
    private final RelatorioLogService relatorioLogService;

    @GetMapping("/group-and-count")
    public ResponseEntity<List<RelatorioLogOutputDTO>> groupByDescricaoAndCount() {
        return ResponseEntity.status(HttpStatus.OK).body(relatorioLogService.groupByDescricaoAndCount());
    }
}
