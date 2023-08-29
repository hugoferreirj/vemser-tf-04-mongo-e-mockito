package br.com.dbc.wbhealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class WbHealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(WbHealthApplication.class, args);
    }

}
