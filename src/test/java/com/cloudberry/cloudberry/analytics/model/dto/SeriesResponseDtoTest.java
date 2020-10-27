package com.cloudberry.cloudberry.analytics.model.dto;

import com.cloudberry.cloudberry.config.JacksonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

class SeriesResponseDtoTest {

    ObjectMapper mapper = new JacksonConfig().objectMapper();

    @Test
    public void serializationTest() throws JsonProcessingException {
        var emptyResponse = new SeriesResponseDto(List.of(), Optional.empty());
        var emptyResponseString = mapper.writeValueAsString(emptyResponse);

        assertThat(emptyResponseString, Matchers.equalTo("{\"series\":[],\"averageSeries\":null}"));
    }

}