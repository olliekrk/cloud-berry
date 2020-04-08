package com.cloudberry.cloudberry.config.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {
//
//    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
//    public KafkaStreamsConfiguration kStreamsConfigs() {
//        Map<String, Object> configs = new HashMap<>();
//        return new KafkaStreamsConfiguration(configs);
//    }

//    @Bean
//    public KStream<?, ?> someStream(StreamsBuilder kStreamBuilder) {
//        KStream<?, ?> stream = kStreamBuilder.stream(KafkaTopics.PROBLEM_DEFINITION_TOPIC);
//        return stream;
//    }

}
