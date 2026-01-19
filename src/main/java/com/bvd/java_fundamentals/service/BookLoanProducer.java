package com.bvd.java_fundamentals.service;

import com.bvd.java_fundamentals.model.BookLoan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookLoanProducer {

    private final KafkaTemplate<String, BookLoan> kafkaTemplate;
    private final String topic;

    public BookLoanProducer(
            KafkaTemplate<String, BookLoan> kafkaTemplate,
            @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(BookLoan loan) {
        kafkaTemplate.send(topic, loan.getLoanId(), loan);
    }
}
