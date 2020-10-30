package com.cloudberry.cloudberry.parsing.model.csv;

import com.cloudberry.cloudberry.parsing.model.UploadDetails;
import lombok.Value;
import org.apache.commons.csv.CSVFormat;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Details for CSV file containing single log type data.
 * <p>
 * (!) To import CSV data, one must beforehand save configuration with ID which is then used during file import.
 */
@Value
public class CsvUploadDetails implements UploadDetails {

    /**
     * Specifies which columns of imported data should be treated as tags (indexed).
     */
    List<String> tagsNames;

    /**
     * Required to link uploaded data with configuration file imported separately, i.e. via Metadata API.
     */
    ObjectId configurationId;

    /**
     * Computation id of uploaded data. If none is given, then it is assumes that data is from new computation.
     */
    @Nullable
    ObjectId computationId;

    /**
     * Measurement name with which the data should be marked. If none is given, default one will be used.
     */
    @Nullable
    String measurementName;

    /**
     * Csv column headers may be given explicitly instead of reading 1st line of CSV file as headers.
     */
    @Nullable
    List<String> headers;

    public CSVFormat determineCsvFormat() {
        if (headers == null) {
            return CSVFormat.DEFAULT
                    .withFirstRecordAsHeader();
        } else {
            return CSVFormat.DEFAULT
                    .withHeader(headers.toArray(String[]::new))
                    .withSkipHeaderRecord(false);
        }
    }
}
