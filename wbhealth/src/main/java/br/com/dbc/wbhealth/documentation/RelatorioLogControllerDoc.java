package br.com.dbc.wbhealth.documentation;

import br.com.dbc.wbhealth.exceptions.DataInvalidaException;
import br.com.dbc.wbhealth.model.dto.relatoriolog.RelatorioLogOutputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RelatorioLogControllerDoc {
    @Operation(summary = "Conta a quantidade de logs separados agrupados pela descrição.", description = "Conta a quantidade de logs separados agrupados pela descrição dos logs.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna a quantidade de cada tipo de descrição de logs."),
                    @ApiResponse(responseCode = "400", description = "Data inválida."),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso."),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção.")
            }
    )
    @GetMapping("/group-and-count")
    public ResponseEntity<List<RelatorioLogOutputDTO>> groupByDescricaoAndCount();

    @Operation(summary = "Conta a quantidade de logs, de um dia específico, agrupados pela descrição.", description = "Conta a quantidade de logs, de um dia específico, agrupados pela descrição dos logs.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna a quantidade de cada tipo de descrição de logs por data."),
                    @ApiResponse(responseCode = "400", description = "Data inválida."),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso."),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção.")
            }
    )
    @GetMapping("/group-and-count-by-date")
    public ResponseEntity<List<RelatorioLogOutputDTO>> groupByDescricaoAndCountByDate(@RequestParam String data) throws DataInvalidaException;
}
