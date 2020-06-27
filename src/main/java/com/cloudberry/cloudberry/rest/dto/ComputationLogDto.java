package com.cloudberry.cloudberry.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class ComputationLogDto {
    private Instant time;
    private Map<String, Object> fields;
    private Map<String, String> tags;
}
