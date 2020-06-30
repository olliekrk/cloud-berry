package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final InfluxDataAccessor influxDataAccessor;

    public void getMeanAndStdOfGroupedData(String comparedField,
                                           String measurementName,
                                           String metaMeasurementName,
                                           @Nullable String bucketName,
                                           @Nullable String metaBucketName,
                                           Map<String, String> metaTags) {

        // todo: return values

        influxDataAccessor.getMeanAndStdOfGroupedData(
                comparedField,
                measurementName,
                metaMeasurementName,
                bucketName,
                metaBucketName,
                metaTags);

        /*
        * 1. przefiltruj po tagach, jesli w jakims entry jest: !entry.value.isEmpty()
        * 2. zgrupuj po tagach
        * 3. wez tylko pole comparedField + czas + tagi
        *
        * 4. mamy teraz cale tablice ((compared_field), _time, field_value),
        *   4.a. trzeba znormalizowac czasy
        *   4.b. dla czasow z roznych uruchomien, z +- tych samych momentow symulacji, nalezy wziac srednia
        * */
    }

}
