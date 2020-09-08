import requests

CONFIGURATION_ID_HEX = 'configurationIdHex'


class ExperimentComputation:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/computation'

    def get_all(self):
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return response.json()

    def get_by_configuration_id(self, configuration_id: str):
        url = f'{self.base_url}/byConfigurationId'
        response = requests.get(url, params={CONFIGURATION_ID_HEX: configuration_id}, json={})
        return response.json()

    def create(self, configuration_id: str):
        url = f'{self.base_url}/create'
        response = requests.post(url, params={CONFIGURATION_ID_HEX: configuration_id}, json={})
        return response.json()
