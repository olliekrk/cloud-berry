import requests

from .backend import CloudberryApi, CloudberryConfig


class ApiConfiguration(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/apiConfiguration'

    def get_property(self, key: str) -> str:
        return requests.get(url=self.__property_url(key)).text

    def set_property(self, key: str, value: str) -> None:
        requests.put(url=self.__property_url(key), json=value)

    def delete_property(self, key: str) -> None:
        requests.delete(url=self.__property_url(key))

    def __property_url(self, key: str) -> str:
        return f'{self.base_url}/property/{key}'
