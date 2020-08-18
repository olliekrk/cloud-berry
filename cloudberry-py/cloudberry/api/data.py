from typing import List

import requests

from .wrappers import DataPoint, DataSeries
from .config import CloudberryConfig, CloudberryApi


class DataFilters:
    def __init__(self, tags: dict = None, fields: dict = None) -> None:
        self.tags = tags
        self.fields = fields


class Data(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/raw'

    def save_data(self,
                  data: List[DataPoint],
                  measurement_name: str = None,
                  bucket_name: str = None) -> bool:
        url = f'{self.base_url}/save'
        params = Data._build_params(measurement_name, bucket_name)

        converted_data = list(map(lambda d: d.__dict__, data))

        response = requests.post(url, params=params, json=converted_data)
        return response.ok

    def get_data(self,
                 filters: DataFilters,
                 measurement_name: str = None,
                 bucket_name: str = None) -> DataSeries:
        url = f'{self.base_url}/find'
        params = Data._build_params(measurement_name, bucket_name)

        response = requests.post(url, params=params, json=Data._build_filters_dto(filters))
        return DataSeries.from_json(response.json())

    def delete_data(self,
                    filters: DataFilters,
                    measurement_name: str = None,
                    bucket_name: str = None) -> bool:
        url = f'{self.base_url}/delete'
        params = Data._build_params(measurement_name, bucket_name)

        response = requests.post(url, params=params, json=Data._build_filters_dto(filters))
        return response.ok

    @staticmethod
    def _build_params(measurement_name: str,
                      bucket_name: str) -> dict:
        params = {}
        if measurement_name is not None:
            params['measurementName'] = measurement_name
        if bucket_name is not None:
            params['bucketName'] = bucket_name
        return params

    @staticmethod
    def _build_filters_dto(filters: DataFilters) -> dict:
        return {'tagFilters': filters.tags, 'fieldFilters': filters.fields}