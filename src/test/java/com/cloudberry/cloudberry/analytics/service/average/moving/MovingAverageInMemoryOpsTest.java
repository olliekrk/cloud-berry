package com.cloudberry.cloudberry.analytics.service.average.moving;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovingAverageInMemoryOpsTest {
    public static final String VALUE = "value";
    public static final String STDDEV = MovingAverageInMemoryOps.STDDEV_KEY;
    public static final String TIME = InfluxDefaults.Columns.TIME;

    // SIMPLE REGULAR CASES

    @Test
    void itShouldReturnOnePointAverageForOneSeriesAndOnePoint() {
        var now = Instant.now();
        var series = List.of(
                new DataSeries("1", List.of(Map.of(VALUE, 1., TIME, now)))
        );

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        assertThat(result.getData(), hasSize(1));
        assertThat((double) result.getData().get(0).get(VALUE), equalTo(1.));
    }

    @Test
    void itShouldReturnOnePointAverageForMultiSeriesWithOnePoint() {
        var now = Instant.now();
        var series = List.of(
                new DataSeries("1", List.of(Map.of(VALUE, 1., TIME, now))),
                new DataSeries("2", List.of(Map.of(VALUE, 2., TIME, now))),
                new DataSeries("3", List.of(Map.of(VALUE, 3., TIME, now))),
                new DataSeries("4", List.of(Map.of(VALUE, 4., TIME, now)))
        );

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        assertThat(result.getData(), hasSize(1));
        assertThat((double) result.getData().get(0).get(VALUE), equalTo(2.5));
    }

    @Test
    void itShouldReturnOnePointAverageAndZeroStdForMultiSeriesWithOnePoint() {
        var now = Instant.now();
        var series = List.of(
                new DataSeries("1", List.of(Map.of(VALUE, 3., TIME, now))),
                new DataSeries("2", List.of(Map.of(VALUE, 3., TIME, now))),
                new DataSeries("3", List.of(Map.of(VALUE, 3., TIME, now))),
                new DataSeries("4", List.of(Map.of(VALUE, 3., TIME, now)))
        );

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        assertThat(result.getData(), hasSize(1));
        assertThat((double) result.getData().get(0).get(VALUE), equalTo(3.));
        assertThat((double) result.getData().get(0).get(STDDEV), equalTo(0.));
    }

    // ADVANCED REGULAR CASES

    @Test
    void itShouldReturnIdenticalSeriesForOneSeries() {
        var now = Instant.now();
        var seriesOne = new DataSeries("1", List.of(
                Map.of(VALUE, 3., TIME, now),
                Map.of(VALUE, 4., TIME, now.plusSeconds(1)),
                Map.of(VALUE, 5., TIME, now.plusSeconds(2)),
                Map.of(VALUE, 6., TIME, now.plusSeconds(3)),
                Map.of(VALUE, 7., TIME, now.plusSeconds(4)),
                Map.of(VALUE, 8., TIME, now.plusSeconds(5)),
                Map.of(VALUE, 9., TIME, now.plusSeconds(6)),
                Map.of(VALUE, 10., TIME, now.plusSeconds(7))
        ));
        var series = List.of(seriesOne);

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        assertThat(result.getData(), hasSize(seriesOne.getDataSize()));
        result.getData().forEach(p -> assertThat((double) p.get(STDDEV), equalTo(0.)));
    }

    @Test
    void itShouldComputeAverageAndStdProperlyForMultiSeries() {
        var now = Instant.now();
        var seriesOne = new DataSeries("1", List.of(
                Map.of(VALUE, 1., TIME, now),
                Map.of(VALUE, 2., TIME, now.plusSeconds(1)),
                Map.of(VALUE, 3., TIME, now.plusSeconds(2))
        ));
        var seriesTwo = new DataSeries("1", List.of(
                Map.of(VALUE, 100., TIME, now),
                Map.of(VALUE, 200., TIME, now.plusSeconds(1)),
                Map.of(VALUE, 300., TIME, now.plusSeconds(2))
        ));
        var series = List.of(seriesOne, seriesTwo);
        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        var resultData = result.getData();
        assertThat(resultData, hasSize(3));
        assertThat((double) resultData.get(0).get(VALUE), equalTo(50.5));
        assertThat((double) resultData.get(1).get(VALUE), equalTo(101.0));
        assertThat((double) resultData.get(2).get(VALUE), equalTo(151.5));
        result.getData().forEach(p -> assertThat((double) p.get(STDDEV), greaterThan(0.)));
    }

    // EMPTY, INVALID, EDGE CASES

    @Test
    void itShouldIgnoreEmptySeries() {
        var now = Instant.now();
        var series = List.of(
                new DataSeries("1", List.of(Map.of(VALUE, 1., TIME, now))),
                new DataSeries("2", List.of()),
                new DataSeries("3", List.of())
        );

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        assertThat(result.getData(), hasSize(1));
        assertThat((double) result.getData().get(0).get(VALUE), equalTo(1.));
    }

    @Test
    void itShouldBeEmptyOnEmptySeries() {
        var series = List.<DataSeries>of();

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false);
        assertTrue(result.isEmpty());
    }

    @Test
    void itShouldIgnoreSeriesWithNoTimestamps() {
        var now = Instant.now();
        var series = List.of(
                new DataSeries("1", List.of(Map.of(VALUE, 1.))),
                new DataSeries("2", List.of(Map.of(VALUE, 2.))),
                new DataSeries("3", List.of(Map.of(VALUE, 3., TIME, now)))
        );

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        assertThat(result.getData(), hasSize(1));
        assertThat((double) result.getData().get(0).get(VALUE), equalTo(3.));
    }

    @Test
    void itShouldIgnoreSeriesWithNoValues() {
        var now = Instant.now();
        var series = List.of(
                new DataSeries("1", List.of(Map.of(TIME, now))),
                new DataSeries("2", List.of(Map.of(TIME, now))),
                new DataSeries("3", List.of(Map.of(TIME, now, VALUE, 3.)))
        );

        var result = MovingAverageInMemoryOps.movingAverageSeries(series, VALUE, false, false).get();
        assertThat(result.getData(), hasSize(1));
        assertThat((double) result.getData().get(0).get(VALUE), equalTo(3.));
    }
}