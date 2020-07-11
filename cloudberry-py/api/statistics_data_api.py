from .config import CloudberryConfig
import requests


class StatisticsDataApi:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config
        self.base_url = f'{config.get_base_url()}/statistics'

    def compare_evaluations(self,
                            evaluation_ids: list,
                            compared_field: str,
                            measurement_name: str,
                            bucket_name: str = None) -> list:
        url = f'{self.base_url}/compare/evaluations'
        params = StatisticsDataApi.default_comparison_params(compared_field, measurement_name, bucket_name)
        response = requests.post(url=url, params=params, json=evaluation_ids)
        return response.json()

    def compare_evaluations_for_configuration(self,
                                              configuration_id: str,
                                              compared_field: str,
                                              measurement_name: str,
                                              bucket_name: str = None) -> list:
        url = f'{self.base_url}/compare/evaluations/all'
        params = StatisticsDataApi.default_comparison_params(compared_field, measurement_name, bucket_name)
        params['configurationIdHex'] = configuration_id
        response = requests.post(url=url, params=params)
        return response.json()

    def compare_configurations(self,
                               configuration_ids: list,
                               compared_field: str,
                               measurement_name: str,
                               bucket_name: str = None) -> list:
        url = f'{self.base_url}/compare/configurations'
        params = StatisticsDataApi.default_comparison_params(compared_field, measurement_name, bucket_name)
        response = requests.post(url=url, params=params, json=configuration_ids)
        return response.json()

    def compare_configurations_for_experiment(self,
                                              experiment_name: str,
                                              compared_field: str,
                                              measurement_name: str,
                                              bucket_name: str = None) -> list:
        url = f'{self.base_url}/compare/configurations/all'
        params = StatisticsDataApi.default_comparison_params(compared_field, measurement_name, bucket_name)
        params['experimentName'] = experiment_name
        response = requests.post(url=url, params=params)
        return response.json()

    @staticmethod
    def default_comparison_params(compared_field: str,
                                  measurement_name: str,
                                  bucket_name: str = None) -> dict:
        params = {
            'comparedField': compared_field,
            'measurementName': measurement_name,
        }
        if bucket_name is not None:
            params['bucketName'] = bucket_name

        return params
