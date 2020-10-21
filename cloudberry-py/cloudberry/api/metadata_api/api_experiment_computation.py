from typing import List

import requests

from ..model.metadata.experiment import Experiment
from ..model.metadata.experiment_computation import ExperimentComputation
from ..model.metadata.experiment_configuration import ExperimentConfiguration
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

    def find_by_configuration(self, configuration: ExperimentConfiguration) -> List[ExperimentComputation]:
        url = f'{self.base_url}/byConfigurationId'
        response = requests.get(url, params={CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex},
                                json={})
        return ExperimentComputationApi._experiment_computation_list_from_json(response.json())

    def find_by_experiment(self, experiment: Experiment) -> List[ExperimentComputation]:
        url = f'{self.base_url}/byExperimentId'
        params = {EXPERIMENT_ID_HEX: experiment.experiment_id_hex}
        response = requests.get(url, params=params, json={})
        return ExperimentComputationApi._experiment_computation_list_from_json(response.json())

    def create(self, configuration: ExperimentConfiguration) -> ExperimentComputation:
        url = f'{self.base_url}/create'
        response = requests.post(url, params={CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex},
                                 json={})
        return ExperimentComputation.from_json(response.json())

    def delete(self, computation: ExperimentComputation) -> bool:
        url = f'{self.base_url}/deleteById'
        params = {COMPUTATION_ID_HEX: computation.computation_id_hex}
        response = requests.delete(url, params=params, json={})
        return response.ok

    @staticmethod
    def _experiment_computation_list_from_json(json_dict) -> List[ExperimentComputation]:
        return list(map(lambda json: ExperimentComputation.from_json(json), json_dict))
