package com.example.stream.consumer

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class EventConsumer {
    
    private val logger: Logger = LoggerFactory.getLogger(EventConsumer::class.java)
    
    @KafkaListener(topics = ["user-events"], groupId = "stream-processor-group")
    fun consumeUserEvent(message: String) {
        logger.info("Consuming user event: $message")
        // Process user event (e.g., user created, updated, deleted)
        // This could update analytics tables, trigger notifications, etc.
    }
    
    @KafkaListener(topics = ["order-events"], groupId = "stream-processor-group")
    fun consumeOrderEvent(message: String) {
        logger.info("Consuming order event: $message")
        // Process order event (e.g., order created, status updated)
    }
    
    @KafkaListener(topics = ["payment-events"], groupId = "stream-processor-group")
    fun consumePaymentEvent(message: String) {
        logger.info("Consuming payment event: $message")
        // Process payment event (e.g., payment completed, failed, refunded)
    }
    
    @KafkaListener(topics = ["inventory-events"], groupId = "stream-processor-group")
    fun consumeInventoryEvent(message: String) {
        logger.info("Consuming inventory event: $message")
        // Process inventory event (e.g., stock updated, reserved, committed)
    }
}