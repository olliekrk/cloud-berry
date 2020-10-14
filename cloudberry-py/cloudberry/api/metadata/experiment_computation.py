import requests

from .constants import *


class ExperimentComputation:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/computation'

    def find_all(self):
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return response.json()

    def find_by_id(self, computation_id: str):
        url = f'{self.base_url}/byId'
        params = {COMPUTATION_ID_HEX: computation_id}
        response = requests.get(url, params=params, json={})
        return response.json()

    def find_by_configuration_id(self, configuration_id: str):
        url = f'{self.base_url}/byConfigurationId'
        response = requests.get(url, params={CONFIGURATION_ID_HEX: configuration_id}, json={})
        return response.json()

    def find_by_experiment_id(self, experiment_id: str):
        url = f'{self.base_url}/byExperimentId'
        params = {EXPERIMENT_ID_HEX: experiment_id}
        response = requests.get(url, params=params, json={})
        return response.json()

    def create(self, configuration_id: str):
        url = f'{self.base_url}/create'
        response = requests.post(url, params={CONFIGURATION_ID_HEX: configuration_id}, json={})
        return response.json()

    def delete_by_id(self, computation_id: str):
        url = f'{self.base_url}/deleteById'
        params = {COMPUTATION_ID_HEX: computation_id}
        requests.delete(url, params=params, json={})
