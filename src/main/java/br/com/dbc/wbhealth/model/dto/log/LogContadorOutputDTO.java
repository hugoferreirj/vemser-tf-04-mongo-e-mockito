package br.com.dbc.wbhealth.model.dto.log;

import br.com.dbc.wbhealth.model.enumarator.Descricao;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LogContadorOutputDTO {

    private Descricao descricao;
    private Integer quantidade;
}
