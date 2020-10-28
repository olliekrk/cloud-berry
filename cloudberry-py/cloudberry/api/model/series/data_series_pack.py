from typing import Optional, List

from .data_series import DataSeries


class DataSeriesPack:
    def __init__(self, series: List[DataSeries], average_series: Optional[DataSeries]):
        self.series = series
        self.average_series = average_series

    @staticmethod
    def from_json(json: dict):
        return DataSeriesPack(
            series=DataSeries.from_json_list(json['series']),
            average_series=DataSeries.from_json(json['averageSeries'])
        )
