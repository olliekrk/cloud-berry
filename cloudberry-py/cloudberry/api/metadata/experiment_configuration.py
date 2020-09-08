import requests

CONFIGURATION_ID_HEX = 'configurationIdHex'

EXPERIMENT_NAME = 'experimentName'
EXPERIMENT_ID_HEX = 'experimentIdHex'
CONFIGURATION_FILE_NAME = 'configurationFileName'
OVERRIDE_PARAMS = 'overrideParams'


class ExperimentConfiguration:
    def __init__(self, base_url: str) -> None:
        self.base_url = f'{base_url}/configuration'

    def get_all(self):
        url = f'{self.base_url}/all'
        response = requests.get(url, json={})
        return response.json()

    def get_by_configuration_file_name(self, configuration_file_name: str):
        url = f'{self.base_url}/byConfigurationFileName'
        response = requests.get(url, params={CONFIGURATION_FILE_NAME: configuration_file_name}, json={})
        return response.json()

    def get_by_experiment_name(self, name: str):
        url = f'{self.base_url}/byExperimentName'
        response = requests.get(url, params={EXPERIMENT_NAME: name}, json={})
        return response.json()

    def get_or_create(self, experiment_id: str, configuration_file_name: str = None, parameters: dict = None):
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
