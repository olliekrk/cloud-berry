package com.cloudberry.cloudberry.parsing.model.age;

import com.cloudberry.cloudberry.parsing.model.UploadDetails;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Optional;


@Value
public class AgeUploadDetails implements UploadDetails {

    /**
     * A mapping which is required to link AgE's file header line, containing columns' names
     * with corresponding data line(s).
     * <p>
     * i.e.:
     * [WH] -> [W]
     * [SH] -> [S]
     * [BH] -> [B]
     */
    Map<String, String> headersKeys;

    /**
     * An optional mapping which is used to link data line types with corresponding DB' measurements' names.
     * If a key is not found, platform-default measurement name is used during import phase.
     * <p>
     * i.e.:
     * [W] -> workplace_log
     * [S] -> summary_log
     * [B] -> best_solution_log
     */
    Map<String, String> headersMeasurements;

    /**
     * Optional configuration's name with which configuration from uploaded file should be saved in the DB.
     * If none is given ten file is searched for suitable 'configuration name' property.
     */
    @Nullable
    String configurationName;

    public AgeUploadDetails(
            @Nullable Map<String, String> headersKeys,
            @Nullable Map<String, String> headersMeasurements,
            @Nullable String configurationName
    ) {
        this.headersKeys = Optional.ofNullable(headersKeys).orElse(Map.of());
        this.headersMeasurements = Optional.ofNullable(headersMeasurements).orElse(Map.of());
        this.configurationName = configurationName;
    }
}
