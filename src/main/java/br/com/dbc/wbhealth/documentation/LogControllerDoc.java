package br.com.dbc.wbhealth.documentation;

import br.com.dbc.wbhealth.model.dto.log.LogOutputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface LogControllerDoc {

    @Operation(summary = "Retorna uma lista de logs paginados", description = "Retorna uma lista de logs paginados")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna uma lista de logs"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping
    ResponseEntity<Page<LogOutputDTO>> findAll(@RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "5") Integer quantidadeLogs);
}
