from typing import List

import requests

from ..model.metadata.experiment_computation import ExperimentComputation
from ...api.constants.constants import *


class ExperimentComputationApi:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/computation'

    def find_all(self) -> List[ExperimentComputation]:
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return ExperimentComputationApi._experiment_computation_list_from_json(response.json())

    def find_by_id(self, computation_id: str) -> ExperimentComputation:
        url = f'{self.base_url}/byId'
        params = {COMPUTATION_ID_HEX: computation_id}
        response = requests.get(url, params=params, json={})
        return ExperimentComputation.from_json(response.json())

    def find_by_configuration_id(self, configuration_id: str) -> List[ExperimentComputation]:
        url = f'{self.base_url}/byConfigurationId'
        response = requests.get(url, params={CONFIGURATION_ID_HEX: configuration_id}, json={})
        return ExperimentComputationApi._experiment_computation_list_from_json(response.json())

    def find_by_experiment_id(self, experiment_id: str) -> List[ExperimentComputation]:
        url = f'{self.base_url}/byExperimentId'
        params = {EXPERIMENT_ID_HEX: experiment_id}
        response = requests.get(url, params=params, json={})
        return ExperimentComputationApi._experiment_computation_list_from_json(response.json())

    def create(self, configuration_id: str) -> ExperimentComputation:
        url = f'{self.base_url}/create'
        response = requests.post(url, params={CONFIGURATION_ID_HEX: configuration_id}, json={})
        return ExperimentComputation.from_json(response.json())

    def delete_by_id(self, computation_id: str) -> bool:
        url = f'{self.base_url}/deleteById'
        params = {COMPUTATION_ID_HEX: computation_id}
        response = requests.delete(url, params=params, json={})
        return response.ok

    @staticmethod
    def _experiment_computation_list_from_json(json_dict) -> List[ExperimentComputation]:
        return list(map(lambda json: ExperimentComputation.from_json(json), json_dict))
