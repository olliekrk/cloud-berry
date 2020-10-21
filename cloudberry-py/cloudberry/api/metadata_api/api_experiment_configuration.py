from typing import List

import requests

from ..model.metadata.experiment import Experiment
from ..model.metadata.experiment_computation import ExperimentComputation
from ..model.metadata.experiment_configuration import ExperimentConfiguration
from ...api.constants.constants import *

EXPERIMENT_NAME = 'experimentName'
CONFIGURATION_FILE_NAME = 'configurationFileName'
OVERRIDE_PARAMS = 'overrideParams'


class ExperimentConfigurationApi:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/configuration'

    def find_all(self) -> List[ExperimentConfiguration]:
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return ExperimentConfigurationApi._experiment_configuration_list_from_json(response.json())

    def find_by_id(self, configuration_id: str) -> ExperimentConfiguration:
        url = f'{self.base_url}/byId'
        params = {CONFIGURATION_ID_HEX: configuration_id}
        response = requests.get(url, params=params, json={})
        return ExperimentConfiguration.from_json(response.json())

    def find_by_computation(self, computation: ExperimentComputation) -> ExperimentConfiguration:
        url = f'{self.base_url}/byComputationId'
        params = {COMPUTATION_ID_HEX: computation.computation_id_hex}
        response = requests.get(url, params=params, json={})
        return ExperimentConfiguration.from_json(response.json())

    def find_by_experiment(self, experiment: Experiment) -> List[ExperimentConfiguration]:
        url = f'{self.base_url}/byExperimentId'
        params = {EXPERIMENT_ID_HEX: experiment.experiment_id_hex}
        response = requests.get(url, params=params, json={})
        return ExperimentConfigurationApi._experiment_configuration_list_from_json(response.json())

    def find_by_configuration_file_name(self, configuration_file_name: str) -> List[ExperimentConfiguration]:
        url = f'{self.base_url}/byConfigurationFileName'
        response = requests.get(url, params={CONFIGURATION_FILE_NAME: configuration_file_name}, json={})
        return ExperimentConfigurationApi._experiment_configuration_list_from_json(response.json())

    def find_by_experiment_name(self, name: str) -> List[ExperimentConfiguration]:
        url = f'{self.base_url}/byExperimentName'
        response = requests.get(url, params={EXPERIMENT_NAME: name}, json={})
        return ExperimentConfigurationApi._experiment_configuration_list_from_json(response.json())

    def find_or_create(self, experiment: Experiment, configuration_file_name: str = None,
                       parameters: dict = None) -> ExperimentConfiguration:
        url = f'{self.base_url}/findOrCreate'
        response = requests.post(url,
                                 params={EXPERIMENT_ID_HEX: experiment.experiment_id_hex,
                                         CONFIGURATION_FILE_NAME: configuration_file_name},
                                 json=parameters)
        return ExperimentConfiguration.from_json(response.json())

    def update(self, configuration: ExperimentConfiguration, configuration_file_name: str = None,
               parameters: dict = None,
               override_params: bool = None) -> ExperimentConfiguration:
        url = f'{self.base_url}/update'
        params = {CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex,
                  CONFIGURATION_FILE_NAME: configuration_file_name,
                  OVERRIDE_PARAMS: override_params}
        response = requests.put(url, params=params, json=parameters)
        return ExperimentConfiguration.from_json(response.json())

    def delete(self, configuration: ExperimentConfiguration) -> bool:
        url = f'{self.base_url}/deleteById'
        params = {COMPUTATION_ID_HEX: configuration.experiment_configuration_id_hex}
        response = requests.delete(url, params=params, json={})
        return response.ok

    @staticmethod
    def _experiment_configuration_list_from_json(json_dict) -> List[ExperimentConfiguration]:
        return list(map(lambda json: ExperimentConfiguration.from_json(json), json_dict))
