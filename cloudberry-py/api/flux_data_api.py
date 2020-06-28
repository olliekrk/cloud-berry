from .config import CloudberryConfig
import requests


class FluxDataApi:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config

    def query(self, raw_query: str, raw=False):
        base_url = self.config.get_base_url()
        url = f'{base_url}/flux/query'
        r = requests.post(url, data=raw_query)
        if raw:
            return r.text
        else:
            return r.json()
