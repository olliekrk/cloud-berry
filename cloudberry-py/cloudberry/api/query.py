import requests

from .backend import CloudberryConfig, CloudberryApi
from .model import DataSeries


class Query(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/flux'

    def query_series(self, raw_query: str) -> DataSeries:
        r = requests.post(f'{self.base_url}/querySeries', data=raw_query)
        return DataSeries.from_json(r.json())
