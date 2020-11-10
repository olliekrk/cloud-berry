from typing import List

import pandas as pd

from ..str_generator import auto_str


@auto_str
class SeriesInfo:
    def __init__(self, series_name: str, series_id: str):
        self.series_name = series_name
        self.series_id = series_id


class DataSeries:
    def __init__(self, series_info: SeriesInfo, data: list, meta_ids=None) -> None:
        self.series_info = series_info
        self.data = data
        self.meta_ids = meta_ids  # MetaIds

    def merge_values_with(self,
                          other_series,
                          merging_key: str,
                          new_series_name: str = None):
        one_df = self.as_data_frame
        second_df = other_series.as_data_frame
        result_df = pd.concat([one_df, second_df]).pivot(index=merging_key, columns='series_name')
        new_series_info = self.series_info
        if new_series_name is not None:
            new_series_info.series_name = new_series_name
        return DataSeries(new_series_info, result_df.to_dict())

    @property
    def as_data_frame(self) -> pd.DataFrame:
        df = pd.DataFrame(self.data)
        df['series_name'] = self.series_info.series_name
        if self.meta_ids is not None:
            df['experiment_id'] = self.meta_ids.experiment_id
            df['configuration_id'] = self.meta_ids.configuration_id
            df['computation_id'] = self.meta_ids.computation_id
        return df

    @staticmethod
    def get_data_series(raw_series: list) -> list:
        data_series = []
        for series in raw_series:
            data_series.append(DataSeries(series['seriesName'], series['data']))

        return data_series

    @staticmethod
    def from_json_list(data_series_jsons: List[dict]):
        return [DataSeries.from_json(ds) for ds in data_series_jsons]

    @staticmethod
    def from_json(data_series_json: dict):
        series_info_ = data_series_json['seriesInfo']
        return DataSeries(series_info=SeriesInfo(series_info_['name'], series_info_['id']),
                          data=data_series_json['data'])
