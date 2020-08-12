from typing import List

import requests

from .wrappers import DataPoint
from .config import CloudberryConfig, CloudberryApi


class DataFilters:
    def __init__(self, tags: dict, fields: dict) -> None:
        self.tags = tags
        self.fields = fields


class Data(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/raw'

    def save_data_points(self,
                         computation_data: List[DataPoint],
                         measurement_name: str = None,
                         bucket_name: str = None) -> bool:
        url = f'{self.base_url}/save/{measurement_name}'
        params = {}
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        converted_data = list(map(lambda d: d.__dict__, computation_data))
        response = requests.post(url, params=params, json=converted_data)
        return response.ok

    def get_measurement_data(self,
                             filters: DataFilters,
                             measurement_name: str,
                             bucket_name: str = None):
        url = f'{self.base_url}/find/{measurement_name}'
        params = {}
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        response = requests.post(url, params=params, json={'tagFilters': filters.tags, 'fieldFilters': filters.fields})
        return response.json()

    def delete_data(self,
                    measurement_name: str,
                    bucket_name: str = None) -> bool:
        url = f'{self.base_url}/{measurement_name}'
        params = {}
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        response = requests.delete(url, params=params)
        return response.ok
