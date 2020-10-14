import requests

from api.metadata import EXPERIMENT_ID_HEX, CONFIGURATION_ID_HEX, COMPUTATION_ID_HEX

EXPERIMENT_NAME = 'experimentName'
CONFIGURATION_FILE_NAME = 'configurationFileName'
OVERRIDE_PARAMS = 'overrideParams'


class ExperimentConfiguration:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/configuration'

    def find_all(self):
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return response.json()

    def find_by_id(self, configuration_id: str):
        url = f'{self.base_url}/byId'
        params = {CONFIGURATION_ID_HEX: configuration_id}
        response = requests.get(url, params=params, json={})
        return response.json()

    def find_by_computation_id(self, computation_id: str):
        url = f'{self.base_url}/byComputationId'
        params = {COMPUTATION_ID_HEX: computation_id}
        response = requests.get(url, params=params, json={})
        return response.json()

    def find_by_experiment_id(self, experiment_id: str):
        url = f'{self.base_url}/byExperimentId'
        params = {EXPERIMENT_ID_HEX: experiment_id}
        response = requests.get(url, params=params, json={})
        return response.json()

    def find_by_configuration_file_name(self, configuration_file_name: str):
        url = f'{self.base_url}/byConfigurationFileName'
        response = requests.get(url, params={CONFIGURATION_FILE_NAME: configuration_file_name}, json={})
        return response.json()

    def find_by_experiment_name(self, name: str):
        url = f'{self.base_url}/byExperimentName'
        response = requests.get(url, params={EXPERIMENT_NAME: name}, json={})
        return response.json()

    def find_or_create(self, experiment_id: str, configuration_file_name: str = None, parameters: dict = None):
        url = f'{self.base_url}/getOrCreate'
        response = requests.post(url,
                                 params={EXPERIMENT_ID_HEX: experiment_id,
                                         CONFIGURATION_FILE_NAME: configuration_file_name},
                                 json=parameters)
        return response.json()

    def update(self, configuration_id: str, configuration_file_name: str = None, parameters: dict = None,
               override_params: bool = None):
        url = f'{self.base_url}/update'
        params = {CONFIGURATION_ID_HEX: configuration_id, CONFIGURATION_FILE_NAME: configuration_file_name,
                  OVERRIDE_PARAMS: override_params}
        response = requests.put(url, params=params, json=parameters)
        return response.json()

    def delete_by_id(self, configuration_id: str):
        url = f'{self.base_url}/deleteById'
        params = {COMPUTATION_ID_HEX: configuration_id}
        requests.delete(url, params=params, json={})
