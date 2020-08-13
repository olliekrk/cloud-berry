import requests
from typing import List

from .config import CloudberryApi, CloudberryConfig
from .wrappers import DataSeries


class Analytics(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/statistics'

    def compare_evaluations(self,
                            evaluation_ids: List[str],
                            compared_field: str,
                            measurement_name: str = None,
                            bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/evaluations'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        response = requests.post(url=url, params=params, json=evaluation_ids)
        return Analytics._wrap_response(response)

    def compare_evaluations_for_configuration(self,
                                              configuration_id: str,
                                              compared_field: str,
                                              measurement_name: str = None,
                                              bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/evaluations/all'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        params['configurationIdHex'] = configuration_id
        response = requests.post(url=url, params=params)
        return Analytics._wrap_response(response)

    def compare_configurations(self,
                               configuration_ids: list,
                               compared_field: str,
                               measurement_name: str = None,
                               bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/configurations'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        response = requests.post(url=url, params=params, json=configuration_ids)
        return Analytics._wrap_response(response)

    def compare_configurations_for_experiment(self,
                                              experiment_name: str,
                                              compared_field: str,
                                              measurement_name: str = None,
                                              bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/configurations/all'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        params['experimentName'] = experiment_name
        response = requests.post(url=url, params=params)
        return Analytics._wrap_response(response)

    @staticmethod
    def _wrap_response(response: requests.Response):
        return DataSeries.from_json_list(response.json())

    @staticmethod
    def _as_comparison_params(compared_field: str,
                              measurement_name: str = None,
                              bucket_name: str = None) -> dict:
        params = {
            'comparedField': compared_field,
        }
        if measurement_name is not None:
            params['measurementName'] = measurement_name
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        return params
