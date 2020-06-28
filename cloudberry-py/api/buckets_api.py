import requests

from .config import CloudberryConfig


class BucketsApi:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config
        self.base_url = f'{self.config.get_base_url()}/buckets'

    def delete_bucket(self, bucket_name):
        response = requests.delete(f'{self.base_url}/{bucket_name}')
        return response.json()
