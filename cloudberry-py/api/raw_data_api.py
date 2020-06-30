from typing import List

import requests

from . import ComputationData, LogFilters
from .config import CloudberryConfig


class RawDataApi:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config
        self.base_url = f'{self.config.get_base_url()}/raw'

    def save_measurement_data(self,
                              computation_data: List[ComputationData],
                              measurement_name: str,
                              bucket_name: str = None) -> bool:
        url = f'{self.base_url}/save/{measurement_name}'
        params = {}
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        converted_data = list(map(lambda data: data.__dict__, computation_data))
        response = requests.post(url, params=params, json=converted_data)
        return response.ok

    def get_measurement_data(self,
                             filters: LogFilters,
                             measurement_name: str,
                             bucket_name: str = None):
        url = f'{self.base_url}/find/{measurement_name}'
        params = {}
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        response = requests.post(url, params=params, json={'tagFilters': filters.tags, 'fieldFilters': filters.fields})
        return response.json()

    def delete_measurement_data(self,
                                measurement_name: str,
                                bucket_name: str = None) -> bool:
        url = f'{self.base_url}/{measurement_name}'
        params = {}
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        response = requests.delete(url, params=params)
        return response.ok
