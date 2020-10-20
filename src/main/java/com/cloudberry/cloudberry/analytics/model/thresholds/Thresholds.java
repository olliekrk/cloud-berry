package com.cloudberry.cloudberry.analytics.model.thresholds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import lombok.Value;
import org.springframework.lang.Nullable;

@Value
@ToString
public class Thresholds {
    @Nullable
    Double upper;
    @Nullable
    Double lower;

    @JsonCreator
    public Thresholds(
            @Nullable @JsonProperty("upper") Double upper,
            @Nullable @JsonProperty("lower") Double lower
    ) {
        this.upper = upper;
        this.lower = lower;
    }

    public boolean isValid() {
        if (upper == null && lower == null) {
            return false;
        } else {
            return upper == null || lower == null || lower <= upper; // lower threshold must be <= than upper
        }
    }
}
