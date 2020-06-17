from .config import CloudberryConfig
import requests


class FluxApi:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config

    def query(self, raw_query: str):
        base_url = self.config.get_base_url()
        url = f'{base_url}/flux/query'
        r = requests.post(url, json={'rawQuery': raw_query})
        return r.json()
