import requests
from typing import List

from .config import CloudberryApi, CloudberryConfig
from .exceptions import CloudberryException, CloudberryConnectionException
from .wrappers import DataSeries


class Analytics(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/statistics'

    def compare_computations(self,
                            computation_ids: List[str],
                            compared_field: str,
                            measurement_name: str = None,
                            bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/computations'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        return Analytics._wrap_request(lambda: requests.post(url=url, params=params, json=computation_ids))

    def compare_computations_for_configuration(self,
                                              configuration_id: str,
                                              compared_field: str,
                                              measurement_name: str = None,
                                              bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/computations/all'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        params['configurationIdHex'] = configuration_id
        return Analytics._wrap_request(lambda: requests.post(url=url, params=params))

    def compare_configurations(self,
                               configuration_ids: list,
                               compared_field: str,
                               measurement_name: str = None,
                               bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/configurations'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        return Analytics._wrap_request(lambda: requests.post(url=url, params=params, json=configuration_ids))

    def compare_configurations_for_experiment(self,
                                              experiment_name: str,
                                              compared_field: str,
                                              measurement_name: str = None,
                                              bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/compare/configurations/all'
        params = Analytics._as_comparison_params(compared_field, measurement_name, bucket_name)
        params['experimentName'] = experiment_name
        return Analytics._wrap_request(lambda: requests.post(url=url, params=params))

    @staticmethod
    def _wrap_request(request_lambda):
        try:
            response = request_lambda()
            return Analytics._wrap_response(response)
        except requests.RequestException as e:
            raise CloudberryConnectionException(e)

    @staticmethod
    def _wrap_response(response: requests.Response):
        if response.ok:
            return DataSeries.from_json_list(response.json())
        else:
            raise CloudberryException("Server request has failed", response.raise_for_status())

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
