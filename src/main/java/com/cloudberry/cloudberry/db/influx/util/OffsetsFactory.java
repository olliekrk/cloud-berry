package com.cloudberry.cloudberry.db.influx.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public abstract class OffsetsFactory {

    private static final ZoneId zoneId = ZoneId.systemDefault();

    public static OffsetDateTime epoch() {
        return truncateToUtcSeconds(OffsetDateTime.ofInstant(Instant.EPOCH, zoneId));
    }

    public static OffsetDateTime now() {
        return truncateToUtcSeconds(OffsetDateTime.now(zoneId));
    }

    private static OffsetDateTime truncateToUtcSeconds(OffsetDateTime offset) {
        return offset
                .truncatedTo(ChronoUnit.SECONDS)
                .withOffsetSameLocal(ZoneOffset.UTC);
    }

}
