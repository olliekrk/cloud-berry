import requests

from .config import CloudberryConfig, CloudberryApi


class Buckets(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/buckets'

    def delete_bucket(self, bucket_name) -> bool:
        response = requests.delete(f'{self.base_url}/{bucket_name}')
        return response.ok
