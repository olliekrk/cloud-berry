import requests

OVERRIDE_PARAMS = 'overrideParams'
EXPERIMENT_ID_HEX = 'experimentIdHex'
NAME = 'name'


class Experiment:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/experiment'

    def get_all(self):
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return response.json()

    def get_by_name(self, name: str):
        url = f'{self.base_url}/byName'
        response = requests.get(url, params={NAME: name}, json={})
        return response.json()

    def get_or_create(self, name: str, parameters: dict = None):
        url = f'{self.base_url}/getOrCreate'
        response = requests.post(url, params={NAME: name}, json=parameters)
        return response.json()

    def update(self, experiment_id: str, name: str = None, parameters: dict = None, override_params: bool = None):
        url = f'{self.base_url}/update'
        params = {EXPERIMENT_ID_HEX: experiment_id, NAME: name, OVERRIDE_PARAMS: override_params}
        response = requests.put(url, params=params, json=parameters)
        return response.json()
