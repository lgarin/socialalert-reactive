package com.bravson.socialalert;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bravson.socialalert.login.UserCredentialRepository;

import reactor.core.publisher.Mono;

@Component
public class SocialAlertUserDetailsService implements ReactiveUserDetailsService {

	@Autowired
	private UserCredentialRepository repository;
	
	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return repository.findByUsername(Mono.just(username)).map(c -> User.withUsername(c.username).password(c.password).build());
	}
}
