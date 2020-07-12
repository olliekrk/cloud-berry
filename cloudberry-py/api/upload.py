import json

import requests

from . import ImportDetails
from .config import CloudberryConfig


class FileUploader:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config

    def upload_file(self, file_name, experiment_name: str, details: ImportDetails):
        with open(file_name, 'rb') as file:
            url = f'{self.config.get_base_url()}/raw/file/{experiment_name}'
            r = requests.post(url, files={
                'file': file,
                'headersKeys': (None, json.dumps(details.headers_keys), 'application/json'),
                'headersMeasurements': (None, json.dumps(details.headers_measurements), 'application/json')
            })
            return r.json()
