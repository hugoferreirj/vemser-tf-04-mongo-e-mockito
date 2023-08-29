package br.com.dbc.wbhealth.model.dto.log;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class LogOutputDTO {

    private String idLog;

    private Integer idUsuario;

    private LocalDateTime dataHora;

    private String descricao;
}
