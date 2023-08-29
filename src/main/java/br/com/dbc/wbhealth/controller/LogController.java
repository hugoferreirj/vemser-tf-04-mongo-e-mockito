package br.com.dbc.wbhealth.controller;

import br.com.dbc.wbhealth.documentation.LogControllerDoc;
import br.com.dbc.wbhealth.model.dto.log.LogOutputDTO;
import br.com.dbc.wbhealth.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController implements LogControllerDoc {

    private final LogService logService;

    @GetMapping
    public ResponseEntity<Page<LogOutputDTO>> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                      @RequestParam(defaultValue = "5") Integer quantidadeLogs) {
        Page<LogOutputDTO> logPage = logService.findAll(page, quantidadeLogs);
        return new ResponseEntity<>(logPage, HttpStatus.OK);
    }
}
