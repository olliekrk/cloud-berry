package com.cloudberry.cloudberry.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {

    public interface Metadata {
        String PROBLEM_DEFINITION_TOPIC = "problemDefinition";
    }

    public interface Logs {
        String BEST_SOLUTION_TOPIC = "bestSolution";
        String SUMMARY_TOPIC = "summary";
        String WORKPLACE_TOPIC = "workplace";
    }

    @Bean
    public NewTopic problemDefinitionTopic() {
        return defaultTopic(Metadata.PROBLEM_DEFINITION_TOPIC);
    }

    @Bean
    public NewTopic bestSolutionTopic() {
        return defaultTopic(Logs.BEST_SOLUTION_TOPIC);
    }

    @Bean
    public NewTopic summaryTopic() {
        return defaultTopic(Logs.SUMMARY_TOPIC);
    }

    @Bean
    public NewTopic workplaceTopic() {
        return defaultTopic(Logs.WORKPLACE_TOPIC);
    }

    private NewTopic defaultTopic(String topic) {
        return TopicBuilder
                .name(topic)
                .build();
    }

}
