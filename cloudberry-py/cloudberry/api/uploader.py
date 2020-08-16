import json

import requests

from .config import CloudberryConfig, CloudberryApi


class UploadDetails:
    """Marker class, used to deliver all necessary information to successfully upload and parse data file"""
    pass


class FileUploader(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)

    def upload_file(self, file_path: str, experiment_name: str, details: UploadDetails):
        """Upload experiment data from file under given path and return ID of saved evaluation"""
        pass


class AgeUploadDetails(UploadDetails):
    def __init__(self,
                 headers_keys=None,
                 headers_measurements=None) -> None:
        self.headers_keys = headers_keys
        self.headers_measurements = headers_measurements


class AgeFileUploader(FileUploader):

    def upload_file(self, file_path: str, experiment_name: str, details: AgeUploadDetails) -> str:
        with open(file_path, 'rb') as file:
            url = f'{self.config.base_url()}/raw/ageFile/{experiment_name}'
            r = requests.post(url, files={
                'file': file,
                'headersKeys': (None, json.dumps(details.headers_keys), 'application/json'),
                'headersMeasurements': (None, json.dumps(details.headers_measurements), 'application/json')
            })
            return r.json()


class CsvUploadDetails(UploadDetails):
    pass


class CsvFileUploader(FileUploader):

    def upload_file(self, file_path: str, experiment_name: str, details: CsvUploadDetails):
        with open(file_path, 'rb') as file:
            url = f'{self.config.base_url()}/raw/csvFile/{experiment_name}'
            r = requests.post(url, files={
                'file': file,
            })
            return r.json()
