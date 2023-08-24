package br.com.dbc.wbhealth.model.entity;

import br.com.dbc.wbhealth.model.enumarator.Descricao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "logs")
public class LogEntity {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String idLog;

    private Integer idUsuario;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private Descricao descricao;
}
