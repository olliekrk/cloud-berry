from .config import CloudberryConfig, CloudberryApi
import requests


class Query(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/flux'

    def query(self, raw_query: str, raw=False):
        r = requests.post(f'{self.base_url}/query', data=raw_query)
        if raw:
            return r.text
        else:
            return r.json()
