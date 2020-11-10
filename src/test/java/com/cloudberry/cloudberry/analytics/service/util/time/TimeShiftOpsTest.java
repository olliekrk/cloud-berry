package com.cloudberry.cloudberry.analytics.service.util.time;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.basic.SeriesInfo;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

class TimeShiftOpsTest {
    public static final String TIME = InfluxDefaults.Columns.TIME;

    @Test
    void timeShiftShouldShiftAllSeriesToEarliest() {
        var now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        var now2 = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(3600);
        var epoch = Instant.EPOCH;
        var seriesNow = new DataSeries(new SeriesInfo("1"), List.of(
                Map.of(TIME, now),
                Map.of(TIME, now.plusSeconds(1)),
                Map.of(TIME, now.plusSeconds(2))
        ));
        var seriesNow2 = new DataSeries(new SeriesInfo("2"), List.of(
                Map.of(TIME, now2),
                Map.of(TIME, now2.plusSeconds(1)),
                Map.of(TIME, now2.plusSeconds(2))
        ));
        var seriesEpoch = new DataSeries(new SeriesInfo("epoch"), List.of(
                Map.of(TIME, epoch),
                Map.of(TIME, epoch.plusSeconds(1)),
                Map.of(TIME, epoch.plusSeconds(2))
        ));

        var shiftedSeries = TimeShiftOps.timeShiftIfPossible(List.of(seriesNow, seriesNow2, seriesEpoch));
        assertThat(shiftedSeries, Matchers.hasSize(3));
        shiftedSeries.forEach(
                s -> assertThat(s.getTimePoints(), Matchers.contains(epoch, epoch.plusSeconds(1), epoch.plusSeconds(2)))
        );
    }
}
