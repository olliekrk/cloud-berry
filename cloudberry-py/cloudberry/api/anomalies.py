from typing import List

import requests

from .backend import CloudberryApi, CloudberryConfig
from .model import AnomalyReport
from .model.metadata import ExperimentComputation, get_ids_for_computations


class Anomalies(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/anomalies'

    def get_report(self,
                   field_name: str,
                   computation: ExperimentComputation,
                   measurement_name: str = None,
                   bucket_name: str = None) -> AnomalyReport:
        return self.get_reports(field_name, [computation], measurement_name, bucket_name)[0]

    def get_reports(self,
                    field_name: str,
                    computations: List[ExperimentComputation],
                    measurement_name: str = None,
                    bucket_name: str = None) -> List[AnomalyReport]:
        url = f'{self.base_url}/report/bulk'
        params = Anomalies._query_params(field_name, measurement_name, bucket_name)
        response = requests.post(url=url, params=params, json=get_ids_for_computations(computations))
        return list(map(lambda json: AnomalyReport.from_json(json), response.json()))

    @staticmethod
    def _query_params(field_name: str,
                      measurement_name: str = None,
                      bucket_name: str = None) -> dict:
        params = {
            'fieldName': field_name
        }
        if measurement_name is not None:
            params['measurementName'] = measurement_name
        if bucket_name is not None:
            params['bucketName'] = bucket_name
        return params
