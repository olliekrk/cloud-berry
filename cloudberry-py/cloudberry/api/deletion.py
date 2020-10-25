import requests

from .backend import CloudberryApi, CloudberryConfig
from .data import Data
from .model.metadata import *

COMPUTATION_IDS_HEX = 'computationIdsHex'
CONFIGURATION_IDS_HEX = 'configurationIdsHex'
EXPERIMENT_IDS_HEX = 'experimentIdsHex'


class Deletion(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/delete'

    def delete_computations(self,
                            computations: List[ExperimentComputation],
                            measurement_name: str = None,
                            bucket_name: str = None):
        url = f'{self.base_url}/computation'
        params = Data.build_params(measurement_name, bucket_name)
        params[COMPUTATION_IDS_HEX] = get_ids_for_computations(computations)
        requests.delete(url=url, params=params, json={})

    def delete_configurations(self,
                              configurations: List[ExperimentConfiguration],
                              measurement_name: str = None,
                              bucket_name: str = None):
        url = f'{self.base_url}/configuration'
        params = Data.build_params(measurement_name, bucket_name)
        params[CONFIGURATION_IDS_HEX] = get_ids_for_configurations(configurations)
        requests.delete(url=url, params=params, json={})

    def delete_experiments(self,
                           experiments: List[Experiment],
                           measurement_name: str = None,
                           bucket_name: str = None):
        url = f'{self.base_url}/experiment'
        params = Data.build_params(measurement_name, bucket_name)
        params[EXPERIMENT_IDS_HEX] = get_ids_for_experiments(experiments)
        requests.delete(url=url, params=params, json={})
