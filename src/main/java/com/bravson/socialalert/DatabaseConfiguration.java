package com.bravson.socialalert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import com.mongodb.WriteConcern;

@Configuration
public class DatabaseConfiguration  {

	@Bean
    public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
	
	@Bean
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter converter) {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);
		mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
		mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		return mongoTemplate;
	}
}
