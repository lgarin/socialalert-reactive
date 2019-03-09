package com.bravson.socialalert.infrastructure.messaging;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventMessagingBridge {

	@Autowired
	AmqpTemplate messaging;
	
	@Autowired
	ApplicationEventPublisher publisher;
	
	@Value("${async.queue}")
	String asyncQueue;
	
	@RabbitListener(queues="${async.queue}")
	protected void onMessage(ApplicationEvent event) {
		publisher.publishEvent(event);
	}
	
	public void sendMessage(ApplicationEvent event) {
		messaging.convertAndSend(asyncQueue, event);
	}
}
