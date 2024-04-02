package com.modutaxi.api.common.mongoDB.repository;

import com.modutaxi.api.common.mongoDB.dao.ExampleMongoDao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExampleMongoRepository extends MongoRepository<ExampleMongoDao, String> {
}
