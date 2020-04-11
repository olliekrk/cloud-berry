package com.cloudberry.cloudberry.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {

    public static final String PROBLEM_DEFINITION_TOPIC = "problemDefinition";
    public static final String BEST_SOLUTION_TOPIC = "bestSolution";
    public static final String SUMMARY_TOPIC = "summary";
    public static final String WORKPLACE_TOPIC = "workplace";

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
