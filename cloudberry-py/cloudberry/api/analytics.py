from typing import List

import pandas as pd
import requests

from .backend import CloudberryApi, CloudberryConfig, CloudberryException, CloudberryConnectionException
from .model import DataSeries, OptimizationGoal, OptimizationKind, TimeUnit, CriteriaMode, Thresholds


class Analytics(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/statistics'

    def compare_computations(self,
                             computation_ids: List[str],
                             field_name: str,
                             measurement_name: str = None,
                             bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/computations/comparison'
        params = Analytics._append_db_params({
            'fieldName': field_name
        }, measurement_name, bucket_name)
        return Analytics._wrap_series_request(lambda: requests.post(url=url, params=params, json=computation_ids))

    def compare_computations_for_configuration(self,
                                               configuration_id: str,
                                               field_name: str,
                                               measurement_name: str = None,
                                               bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/computations/comparison/forConfiguration'
        params = Analytics._append_db_params({
            'fieldName': field_name,
            'configurationIdHex': configuration_id
        }, measurement_name, bucket_name)
        return Analytics._wrap_series_request(lambda: requests.post(url=url, params=params))

    def compare_configurations(self,
                               configuration_ids: list,
                               field_name: str,
                               measurement_name: str = None,
                               bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/configurations/comparison'
        params = Analytics._append_db_params({
            'fieldName': field_name
        }, measurement_name, bucket_name)
        return Analytics._wrap_series_request(lambda: requests.post(url=url, params=params, json=configuration_ids))

    def compare_configurations_for_experiment(self,
                                              experiment_name: str,
                                              field_name: str,
                                              measurement_name: str = None,
                                              bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/configurations/comparison/forExperiment'
        params = Analytics._append_db_params({
            'fieldName': field_name,
            'experimentName': experiment_name
        }, measurement_name, bucket_name)
        return Analytics._wrap_series_request(lambda: requests.post(url=url, params=params))

    def best_n_computations(self,
                            n: int,
                            field_name: str,
                            goal: OptimizationGoal,
                            kind: OptimizationKind,
                            measurement_name: str = None,
                            bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/computations/best'
        params = Analytics._append_db_params({
            'n': n,
            'fieldName': field_name,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, measurement_name, bucket_name)
        return Analytics._wrap_series_request(lambda: requests.get(url=url, params=params))

    def avg_and_stddev_for_computations(self,
                                        computation_ids: List[str],
                                        field_name: str,
                                        interval: int,
                                        time_unit: TimeUnit,
                                        measurement_name: str = None,
                                        bucket_name: str = None) -> DataSeries:
        url = f'{self.base_url}/computations/averageStddev'
        params = Analytics._append_db_params({
            'fieldName': field_name,
            'interval': interval,
            'unit': time_unit.name
        }, measurement_name, bucket_name)

        # todo: simplify code below?
        series = Analytics._wrap_series_request(lambda: requests.post(
            url=url,
            params=params,
            json=computation_ids
        ))

        def get_series(name):
            return list(filter(lambda s: s.series_name == name, series))[0] \
                .as_data_frame \
                .rename(columns={field_name: name}) \
                .drop(columns=['series_name'])

        merged_data = pd.merge(get_series('AVG'), get_series('STDDEV'), on='_time').T.to_dict().values()
        return DataSeries(field_name, merged_data)

    def thresholds_exceeding_computations(self,
                                          field_name: str,
                                          criteria_mode: CriteriaMode,
                                          thresholds: Thresholds,
                                          measurement_name: str = None,
                                          bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/computations/exceedingThresholds'
        params = Analytics._append_db_params({
            'fieldName': field_name,
            'mode': criteria_mode,
        }, measurement_name, bucket_name)
        return Analytics._wrap_series_request(lambda: requests.post(url=url, params=params, json=thresholds))

    @staticmethod
    def _wrap_series_request(request_lambda) -> List[DataSeries]:
        try:
            response = request_lambda()
            return Analytics._wrap_response(response)
        except requests.RequestException as e:
            raise CloudberryConnectionException(e)

    @staticmethod
    def _wrap_response(response: requests.Response) -> List[DataSeries]:
        if response.ok:
            return DataSeries.from_json_list(response.json())
        else:
            raise CloudberryException("Server request has failed", response.raise_for_status())

    @staticmethod
    def _append_db_params(params: dict,
                          measurement_name: str = None,
                          bucket_name: str = None) -> dict:
        if measurement_name is not None:
            params['measurementName'] = measurement_name
        if bucket_name is not None:
            params['bucketName'] = bucket_name
        return params
