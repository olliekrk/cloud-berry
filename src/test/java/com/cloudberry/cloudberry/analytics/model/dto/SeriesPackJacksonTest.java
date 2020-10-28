package com.cloudberry.cloudberry.analytics.model.dto;

import com.cloudberry.cloudberry.config.JacksonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeriesPackJacksonTest {

    final ObjectMapper mapper = new JacksonConfig().objectMapper();
    final String emptyJson = "{\"series\":[],\"averageSeries\":null}";

    @Test
    public void serializationTest() throws JsonProcessingException {
        var emptyResponseString = mapper.writeValueAsString(new SeriesPack(List.of(), Optional.empty()));
        assertThat(emptyResponseString, Matchers.equalTo(emptyJson));
    }

    @Test
    public void deserializationTest() throws JsonProcessingException {
        var pack = mapper.readValue(emptyJson, SeriesPack.class);
        assertTrue(pack.getSeries().isEmpty());
        assertTrue(pack.getAverageSeries().isEmpty());
    }

}