from typing import List

import requests

from .backend import CloudberryConfig, CloudberryApi


class Buckets(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/buckets'

    def get_buckets_names(self) -> List[str]:
        response = requests.get(self.base_url)
        return response.json()

    def delete_bucket(self, bucket_name) -> bool:
        response = requests.delete(f'{self.base_url}/{bucket_name}')
        return response.ok

    def create_bucket(self, bucket_name) -> bool:
        response = requests.post(f'{self.base_url}/{bucket_name}')
        return response.ok
