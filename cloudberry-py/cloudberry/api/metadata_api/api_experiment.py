from typing import List

import requests

from ..model.metadata.experiment import Experiment
from ...api.constants.constants import *

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

    def find_by_computation_id(self, computation_id: str) -> Experiment:
        url = f'{self.base_url}/byComputationId'
        params = {COMPUTATION_ID_HEX: computation_id}
        response = requests.get(url, params=params, json={})
        return Experiment.from_json(response.json())

    def find_by_configuration_id(self, experiment_id: str) -> Experiment:
        url = f'{self.base_url}/byConfigurationId'
        params = {CONFIGURATION_ID_HEX: experiment_id}
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

    def update(self, experiment_id: str, name: str = None, parameters: dict = None,
               override_params: bool = None) -> Experiment:
        url = f'{self.base_url}/update'
        params = {EXPERIMENT_ID_HEX: experiment_id, NAME: name, OVERRIDE_PARAMS: override_params}
        response = requests.put(url, params=params, json=parameters)
        return Experiment.from_json(response.json())

    def delete_by_id(self, experiment_id: str) -> bool:
        url = f'{self.base_url}/deleteById'
        params = {EXPERIMENT_ID_HEX: experiment_id}
        response = requests.delete(url, params=params, json={})
        return response.ok

    @staticmethod
    def _experiment_list_from_json(json_dict) -> List[Experiment]:
        return list(map(lambda json: Experiment.from_json(json), json_dict))
