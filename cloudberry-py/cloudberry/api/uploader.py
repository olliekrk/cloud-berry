import json

import requests

from .config import CloudberryConfig


class UploadDetails:
    def __init__(self,
                 headers_keys=None,
                 headers_measurements=None) -> None:
        self.headers_keys = headers_keys
        self.headers_measurements = headers_measurements


class FileUploader:
    def upload_file(self, file_path: str, experiment_name: str, details: UploadDetails):
        """Upload experiment data from file under given path and return ID of saved evaluation"""
        pass


class AgeFileUploader(FileUploader):

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config

    def upload_file(self, file_name, experiment_name: str, details: UploadDetails) -> str:
        with open(file_name, 'rb') as file:
            url = f'{self.config.base_url()}/raw/file/{experiment_name}'
            r = requests.post(url, files={
                'file': file,
                'headersKeys': (None, json.dumps(details.headers_keys), 'application/json'),
                'headersMeasurements': (None, json.dumps(details.headers_measurements), 'application/json')
            })
            return r.json()


class CsvFileUploader(FileUploader):
    def upload_file(self, file_path: str, experiment_name: str, details: UploadDetails):
        pass  # todo
