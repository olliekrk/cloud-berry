from typing import List

import requests

from .backend import CloudberryApi, CloudberryConfig
from .data import Data

COMPUTATION_IDS_HEX = 'computationIdsHex'
CONFIGURATION_IDS_HEX = 'configurationIdsHex'
EXPERIMENT_IDS_HEX = 'experimentIdsHex'


class Deletion(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/delete'

    def delete_computations(self,
                            computation_ids: List[str],
                            measurement_name: str = None,
                            bucket_name: str = None):
        url = f'{self.base_url}/computation'
        params = Data.build_params(measurement_name, bucket_name)
        params[COMPUTATION_IDS_HEX] = computation_ids
        requests.delete(url=url, params=params, json={})

    def delete_configurations(self,
                              configuration_ids: List[str],
                              measurement_name: str = None,
                              bucket_name: str = None):
        url = f'{self.base_url}/configuration'
        params = Data.build_params(measurement_name, bucket_name)
        params[CONFIGURATION_IDS_HEX] = configuration_ids
        requests.delete(url=url, params=params, json={})

    def delete_experiments(self,
                           experiment_ids: List[str],
                           measurement_name: str = None,
                           bucket_name: str = None):
        url = f'{self.base_url}/experiment'
        params = Data.build_params(measurement_name, bucket_name)
        params[EXPERIMENT_IDS_HEX] = experiment_ids
        requests.delete(url=url, params=params, json={})
