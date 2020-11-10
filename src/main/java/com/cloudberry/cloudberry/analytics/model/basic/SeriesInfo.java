package com.cloudberry.cloudberry.analytics.model.basic;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
public class SeriesInfo {
    @With
    String name;
    String id;

    public SeriesInfo(String name) {
        this.name = name;
        this.id = name;
    }
}
