package br.com.dbc.wbhealth.repository;

import br.com.dbc.wbhealth.model.entity.Log;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends MongoRepository<Log, String> {

}
