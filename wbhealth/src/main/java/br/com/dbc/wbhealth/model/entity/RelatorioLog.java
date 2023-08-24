package br.com.dbc.wbhealth.model.entity;

import br.com.dbc.wbhealth.model.enumarator.Descricao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioLog {
    @Id
    private Descricao descricao;
    private Integer quantidade;

}
