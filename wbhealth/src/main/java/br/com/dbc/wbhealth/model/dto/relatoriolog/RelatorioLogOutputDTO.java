package br.com.dbc.wbhealth.model.dto.relatoriolog;

import br.com.dbc.wbhealth.model.enumarator.Descricao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioLogOutputDTO {
    private Descricao descricao;
    private Integer quantidade;
}
