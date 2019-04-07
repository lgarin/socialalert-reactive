package com.bravson.socialalert;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated().and().csrf().disable();
	}

	/*
	 * @Bean public ReactiveUserDetailsService
	 * userDetailService(UserCredentialRepository repo) { return (username) ->
	 * repo.findByUsername(Mono.just(username)).map(c ->
	 * User.withUsername(c.username).password(c.password).build()); }
	 * 
	 * @Bean public ReactiveAuthenticationManager
	 * authenticationManager(ReactiveUserDetailsService userRepository) { return new
	 * UserDetailsRepositoryReactiveAuthenticationManager(userRepository); }
	 */
}
