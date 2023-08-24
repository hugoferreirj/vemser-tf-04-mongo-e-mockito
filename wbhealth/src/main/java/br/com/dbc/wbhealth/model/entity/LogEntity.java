package br.com.dbc.wbhealth.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document
public class LogEntity {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String idLog;

    private Integer idUsuario;

    private LocalDateTime dataHora;

    private String descricao;
}
