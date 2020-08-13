import pandas as pd


# wrapper classes

class DataPoint:
    def __init__(self, time, fields, tags) -> None:
        self.time = time
        self.fields = fields
        self.tags = tags


class DataSeries:
    def __init__(self, series_name: str, data: list) -> None:
        self.series_name = series_name
        self.data = data

    @property
    def as_data_frame(self) -> pd.DataFrame:
        df = pd.DataFrame(self.data)
        df['series_name'] = self.series_name
        return df

    @staticmethod
    def get_data_series(raw_series: list) -> list:
        data_series = []
        for series in raw_series:
            data_series.append(DataSeries(series['seriesName'], series['data']))

        return data_series

    @staticmethod
    def from_json(data_series_json: dict):
        return DataSeries(series_name=data_series_json['seriesName'], data=data_series_json['data'])
