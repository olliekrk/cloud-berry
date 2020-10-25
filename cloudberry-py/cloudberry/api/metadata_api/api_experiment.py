from typing import List

import requests

from ..constants import *
from ..model.metadata import Experiment, ExperimentConfiguration, ExperimentComputation

OVERRIDE_PARAMS = 'overrideParams'
NAME = 'name'


class ExperimentApi:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/experiment'

    def find_all(self) -> List[Experiment]:
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return ExperimentApi._experiment_list_from_json(response.json())

    def find_by_id(self, experiment_id: str) -> Experiment:
        url = f'{self.base_url}/byId'
        params = {EXPERIMENT_ID_HEX: experiment_id}
        response = requests.get(url, params=params, json={})
        return Experiment.from_json(response.json())

    def find_by_computation(self, computation: ExperimentComputation) -> Experiment:
        url = f'{self.base_url}/byComputationId'
        params = {COMPUTATION_ID_HEX: computation.computation_id_hex}
        response = requests.get(url, params=params, json={})
        return Experiment.from_json(response.json())

    def find_by_configuration(self, configuration: ExperimentConfiguration) -> Experiment:
        url = f'{self.base_url}/byConfigurationId'
        params = {CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex}
        response = requests.get(url, params=params, json={})
        return Experiment.from_json(response.json())

    def find_by_name(self, name: str) -> List[Experiment]:
        url = f'{self.base_url}/byName'
        response = requests.get(url, params={NAME: name}, json={})
        return ExperimentApi._experiment_list_from_json(response.json())

    def find_or_create(self, name: str, parameters: dict = None) -> Experiment:
        url = f'{self.base_url}/findOrCreate'
        response = requests.post(url, params={NAME: name}, json=parameters)
        return Experiment.from_json(response.json())

    def update(self, experiment: Experiment, name: str = None, parameters: dict = None,
               override_params: bool = None) -> Experiment:
        url = f'{self.base_url}/update'
        params = {EXPERIMENT_ID_HEX: experiment.experiment_id_hex, NAME: name, OVERRIDE_PARAMS: override_params}
        response = requests.put(url, params=params, json=parameters)
        return Experiment.from_json(response.json())

    def delete(self, experiment: Experiment) -> bool:
        url = f'{self.base_url}/deleteById'
        params = {EXPERIMENT_ID_HEX: experiment.experiment_id_hex}
        response = requests.delete(url, params=params, json={})
        return response.ok

    @staticmethod
    def _experiment_list_from_json(json_dict) -> List[Experiment]:
        return list(map(lambda json: Experiment.from_json(json), json_dict))
