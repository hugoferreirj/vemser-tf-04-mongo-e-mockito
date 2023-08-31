package br.com.dbc.wbhealth.model.dto.hospital;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalOutputDTO {

    @Schema(description = "Id do Hospital", example = "6")
    private Integer idHospital;

    @Schema(description = "Nome do Hospital", example = "Santa luzia")
    private String nome;

    @Schema(description = "CNPJ do Hospital", example = "32187176000190")
    private String cnpj;

}
