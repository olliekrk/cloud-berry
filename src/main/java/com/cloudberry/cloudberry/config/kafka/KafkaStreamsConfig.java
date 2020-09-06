package com.cloudberry.cloudberry.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;

@Slf4j
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs(KafkaProperties kafkaProperties,
                                                     @Value("${spring.application.name}") String applicationName) {
        var properties = new HashMap<String, Object>(kafkaProperties.getProperties());
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationName);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        return new KafkaStreamsConfiguration(properties);
    }

//    @Bean
//    public KStream<String, Object> kStream(StreamsBuilder kStreamBuilder) {
//        KStream<String, Object> stream = kStreamBuilder.stream(KafkaTopics.Logs.WORKPLACE_TOPIC);
//         how to use: provide processors implementation
//        stream.foreach((_key, value) -> log.info("KStream with WorkplaceEvent: " + value));
//        return stream;
//    }

}
