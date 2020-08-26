from typing import List

import requests

from .backend import CloudberryApi, CloudberryConfig
from .model import AnomalyReport


class Anomalies(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/anomalies'

    def get_report(self,
                   field_name: str,
                   computation_id: str,
                   measurement_name: str = None,
                   bucket_name: str = None) -> AnomalyReport:
        return self.get_reports(field_name, [computation_id], measurement_name, bucket_name)[0]

    def get_reports(self,
                    field_name: str,
                    computation_ids: List[str],
                    measurement_name: str = None,
                    bucket_name: str = None) -> List[AnomalyReport]:
        url = f'{self.base_url}/report/bulk'
        params = Anomalies._query_params(field_name, measurement_name, bucket_name)
        response = requests.post(url=url, params=params, json=computation_ids)
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
