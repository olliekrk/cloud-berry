package com.cloudberry.cloudberry.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicsConfig {

    public static final String PROBLEM_DEFINITION_TOPIC = "problemDefinition";
    public static final String BEST_SOLUTION_TOPIC = "bestSolution";
    public static final String SUMMARY_TOPIC = "summary";
    public static final String WORKPLACE_TOPIC = "workplace";

    private final KafkaProperties kafkaProperties;

    public KafkaTopicsConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic problemDefinitionTopic() {
        return defaultTopic(PROBLEM_DEFINITION_TOPIC);
    }

    @Bean
    public NewTopic bestSolutionTopic() {
        return defaultTopic(BEST_SOLUTION_TOPIC);
    }

    @Bean
    public NewTopic summaryTopic() {
        return defaultTopic(SUMMARY_TOPIC);
    }

    @Bean
    public NewTopic workplaceTopic() {
        return defaultTopic(WORKPLACE_TOPIC);
    }

    private NewTopic defaultTopic(String topic) {
        return TopicBuilder
                .name(topic)
                .build();
    }

}
