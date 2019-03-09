package com.bravson.socialalert;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

	@Bean
	public Queue asyncQueue(@Value("${async.queue}") String name) {
		return new Queue(name, true);
	}
}
