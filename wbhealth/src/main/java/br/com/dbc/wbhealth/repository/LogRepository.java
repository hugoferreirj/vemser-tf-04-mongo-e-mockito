package br.com.dbc.wbhealth.repository;

import br.com.dbc.wbhealth.model.entity.LogEntity;
import br.com.dbc.wbhealth.model.entity.RelatorioLog;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends MongoRepository<LogEntity, String> {

    @Aggregation(pipeline = {
            "{'$group': { '_id': '$descricao', 'quantidade': { '$sum': 1 } } }"
    }
    )
    List<RelatorioLog> groupByDescricaoAndCount();

}
