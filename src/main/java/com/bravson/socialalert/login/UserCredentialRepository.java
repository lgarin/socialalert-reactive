package com.bravson.socialalert.login;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface UserCredentialRepository extends ReactiveMongoRepository<UserCredential, String> {
  
    Mono<UserCredential> findByUsername(Mono<String> username);
}
