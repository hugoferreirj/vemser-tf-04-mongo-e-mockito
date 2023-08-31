package br.com.dbc.wbhealth.model.dto.log;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogOutputDTO {

    private String idLog;

    private Integer idUsuario;

    private LocalDateTime dataHora;

    private String descricao;
}
