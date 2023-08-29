package br.com.dbc.wbhealth.model.entity;

import br.com.dbc.wbhealth.model.enumarator.Descricao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioLogEntity {
    @Id
    private Descricao descricao;
    private Integer quantidade;

}
