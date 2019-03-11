package com.bravson.socialalert;

import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
	/*
	@Bean
	@Profile("dev")
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
	    return http.csrf().disable().authorizeExchange()
	      .anyExchange().permitAll().and().build();
	}
	*/
	/*
	@Bean
	public ReactiveUserDetailsService userDetailService(UserCredentialRepository repo) {
		return (username) -> repo.findByUsername(Mono.just(username)).map(c -> User.withUsername(c.username).password(c.password).build());
	}
	
	@Bean
	public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userRepository) {
	    return new UserDetailsRepositoryReactiveAuthenticationManager(userRepository);
	}
	*/
}
