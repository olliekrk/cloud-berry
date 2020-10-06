import requests

from .backend import CloudberryApi, CloudberryConfig


class ApiConfiguration(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/apiConfiguration'

    def get_property(self, key: str) -> str:
        url = f'{self.base_url}/property/{key}'
        response = requests.get(url=url)
        return response.text()

    def set_property(self, key: str, value: str) -> None:
        url = f'{self.base_url}/property/{key}'
        requests.put(url=url, json=value)
