package com.bvd.java_fundamentals.service;

import com.bvd.java_fundamentals.model.BookLoan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookLoanProducer {

    private static final Logger logger = LoggerFactory.getLogger(BookLoanProducer.class);

    private final KafkaTemplate<String, BookLoan> kafkaTemplate;
    private final String topic;

    public BookLoanProducer(
            KafkaTemplate<String, BookLoan> kafkaTemplate,
            @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        logger.info("BookLoanProducer initialized with topic: {}", topic);
    }

    public void send(BookLoan loan) {
        logger.debug("Sending loan {} to Kafka topic: {}", loan.getLoanId(), topic);
        kafkaTemplate.send(topic, loan.getLoanId(), loan);
    }
}