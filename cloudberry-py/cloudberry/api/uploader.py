import json
from typing import List

import requests

from .backend import CloudberryConfig, CloudberryApi
from .constants import *
from .json_util import JSONUtil
from .model.metadata import ExperimentComputation, ExperimentConfiguration


class UploadDetails:
    """Marker class, used to deliver all necessary information to successfully upload and parse data file"""
    pass


class FileUploader(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)

    def upload_file(self, file_path: str, experiment_name: str, details: UploadDetails) -> ExperimentComputation:
        """Upload experiment data from file under given path and return ID of saved computation"""
        pass


class AgeUploadDetails(UploadDetails):
    def __init__(self,
                 headers_keys=None,
                 headers_measurements=None,
                 configuration_name=None) -> None:
        self.headers_keys = headers_keys
        self.headers_measurements = headers_measurements
        self.configuration_name = configuration_name


class AgeFileUploader(FileUploader):

    def upload_file(self, file_path: str, experiment_name: str, details: AgeUploadDetails) -> ExperimentComputation:
        with open(file_path, 'rb') as file:
            url = f'{self.config.base_url()}/raw/ageFile/{experiment_name}'
            params = {}
            if details.configuration_name is not None:
                params['configurationName'] = details.configuration_name
            r = requests.post(
                url,
                files={
                    'file': file,
                    'headersKeys': (None, json.dumps(details.headers_keys), 'application/json'),
                    'headersMeasurements': (None, json.dumps(details.headers_measurements), 'application/json')
                },
                params=params
            )
            return ExperimentComputation.from_json(r.json())


class CsvUploadDetails(UploadDetails):

    def __init__(self,
                 tags_names,
                 configuration: ExperimentConfiguration,
                 computation: ExperimentComputation = None,
                 measurement_name=None) -> None:
        self.tags_names = tags_names
        self.configuration = configuration
        self.computation = computation
        self.measurement_name = measurement_name


class CsvFileUploader(FileUploader):

    def upload_file_without_headers(self,
                                    file_path: str,
                                    experiment_name: str,
                                    details: CsvUploadDetails,
                                    headers: List[str]) -> ExperimentComputation:
        payload = {
            'tags': CsvFileUploader.__payload_json(details.tags_names),
            'headers': CsvFileUploader.__payload_json(headers),
        }
        params = {
            'hasHeaders': 'false',
        }
        return self.__upload_file(file_path, experiment_name, details, payload, params)

    def upload_file(self,
                    file_path: str,
                    experiment_name: str,
                    details: CsvUploadDetails) -> ExperimentComputation:
        payload = {
            'tags': CsvFileUploader.__payload_json(details.tags_names),
        }
        params = {
            'hasHeaders': 'true',
        }
        return self.__upload_file(file_path, experiment_name, details, payload, params)

    def __upload_file(self,
                      file_path: str,
                      experiment_name: str,
                      details: CsvUploadDetails,
                      payload: dict,
                      params: dict) -> ExperimentComputation:
        with open(file_path, 'rb') as file:
            details_params = CsvFileUploader.__csv_details_to_params(details)
            all_params = {**details_params, **params}
            payload['file'] = file
            upload_url = f'{self.config.base_url()}/raw/csvFile/{experiment_name}'
            r = requests.post(upload_url, files=payload, params=all_params)
            return ExperimentComputation.from_json(r.json())

    @staticmethod
    def __csv_details_to_params(details: CsvUploadDetails):
        params = {CONFIGURATION_ID_HEX: details.configuration.experiment_configuration_id_hex}
        if details.computation is not None:
            params['computationId'] = details.computation.computation_id_hex
        if details.measurement_name is not None:
            params['measurementName'] = details.measurement_name
        return params

    @staticmethod
    def __payload_json(payload):
        return JSONUtil.multipart_payload(payload)
