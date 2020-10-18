import pandas as pd
import requests
from typing import List

from .backend import CloudberryApi, CloudberryConfig, CloudberryException, CloudberryConnectionException
from .json_util import JSONUtil
from .model import DataSeries, OptimizationGoal, OptimizationKind, TimeUnit, CriteriaMode, Thresholds


class Analytics(CloudberryApi):

    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.computations = ComputationsAnalytics(config)
        self.configurations = ConfigurationsAnalytics(config)

    def compare_computations(self,
                             computation_ids: List[str],
                             field_name: str,
                             measurement_name: str = None,
                             bucket_name: str = None) -> List[DataSeries]:
        return self.computations.comparison(computation_ids, field_name, measurement_name, bucket_name)

    def compare_computations_for_configuration(self,
                                               configuration_id: str,
                                               field_name: str,
                                               measurement_name: str = None,
                                               bucket_name: str = None) -> List[DataSeries]:
        return self.computations.comparison_for_configuration(configuration_id, field_name, measurement_name,
                                                              bucket_name)

    def compare_configurations(self,
                               configuration_ids: list,
                               field_name: str,
                               measurement_name: str = None,
                               bucket_name: str = None) -> List[DataSeries]:
        return self.configurations.comparison(configuration_ids, field_name, measurement_name, bucket_name)

    def compare_configurations_for_experiment(self,
                                              experiment_name: str,
                                              field_name: str,
                                              measurement_name: str = None,
                                              bucket_name: str = None) -> List[DataSeries]:
        return self.configurations.comparison_for_experiment(experiment_name, field_name, measurement_name, bucket_name)

    def best_n_computations(self,
                            n: int,
                            field_name: str,
                            goal: OptimizationGoal,
                            kind: OptimizationKind,
                            measurement_name: str = None,
                            bucket_name: str = None) -> List[DataSeries]:
        return self.computations.best_n(n, field_name, goal, kind, measurement_name, bucket_name)

    def best_n_computations_for_configuration(self,
                                              n: int,
                                              field_name: str,
                                              configuration_id: str,
                                              goal: OptimizationGoal,
                                              kind: OptimizationKind,
                                              measurement_name: str = None,
                                              bucket_name: str = None) -> List[DataSeries]:
        return self.computations.best_n_for_configuration(n, field_name, configuration_id, goal, kind, measurement_name,
                                                          bucket_name)

    def best_n_configurations(self,
                              n: int,
                              field_name: str,
                              configuration_ids: List[str],
                              goal: OptimizationGoal,
                              kind: OptimizationKind,
                              measurement_name: str = None,
                              bucket_name: str = None) -> List[DataSeries]:
        return self.configurations.best_n(n, field_name, configuration_ids, goal, kind, measurement_name, bucket_name)

    def best_n_configurations_for_experiment(self,
                                             n: int,
                                             field_name: str,
                                             experiment_name: str,
                                             goal: OptimizationGoal,
                                             kind: OptimizationKind,
                                             measurement_name: str = None,
                                             bucket_name: str = None) -> List[DataSeries]:
        return self.configurations.best_n_for_experiment(n, field_name, experiment_name, goal, kind, measurement_name,
                                                         bucket_name)

    def avg_and_stddev_for_computations(self,
                                        computation_ids: List[str],
                                        field_name: str,
                                        interval: int,
                                        time_unit: TimeUnit,
                                        measurement_name: str = None,
                                        bucket_name: str = None) -> DataSeries:
        return self.computations.avg_and_std_dev(computation_ids, field_name, interval, time_unit, measurement_name,
                                                 bucket_name)

    def thresholds_exceeding_computations(self,
                                          field_name: str,
                                          criteria_mode: CriteriaMode,
                                          thresholds: Thresholds,
                                          measurement_name: str = None,
                                          bucket_name: str = None) -> List[DataSeries]:
        return self.computations.exceeding_thresholds(field_name, criteria_mode, thresholds, measurement_name,
                                                      bucket_name)

    def thresholds_exceeding_computations_for_configuration(self,
                                                            field_name: str,
                                                            configuration_id: str,
                                                            criteria_mode: CriteriaMode,
                                                            thresholds: Thresholds,
                                                            measurement_name: str = None,
                                                            bucket_name: str = None) -> List[DataSeries]:
        self.computations.exceeding_thresholds_for_configuration(field_name, configuration_id, criteria_mode,
                                                                 thresholds, measurement_name, bucket_name)

    def thresholds_exceeding_configurations(self,
                                            field_name: str,
                                            criteria_mode: CriteriaMode,
                                            thresholds: Thresholds,
                                            configuration_ids: List[str],
                                            measurement_name: str = None,
                                            bucket_name: str = None) -> List[DataSeries]:
        return self.configurations.exceeding_thresholds(field_name, criteria_mode, thresholds, configuration_ids,
                                                        measurement_name, bucket_name)

    def thresholds_exceeding_configurations_for_experiment(self,
                                                           field_name: str,
                                                           criteria_mode: CriteriaMode,
                                                           thresholds: Thresholds,
                                                           experiment_name: str,
                                                           measurement_name: str = None,
                                                           bucket_name: str = None) -> List[DataSeries]:
        self.configurations.exceeding_thresholds_for_experiment(field_name, experiment_name, criteria_mode,
                                                                thresholds, measurement_name, bucket_name)


class ComputationsAnalytics(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/statistics/computations'

    def comparison(self,
                   computation_ids: List[str],
                   field_name: str,
                   measurement_name: str = None,
                   bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/comparison'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params, json=computation_ids))

    def comparison_for_configuration(self,
                                     configuration_id: str,
                                     field_name: str,
                                     measurement_name: str = None,
                                     bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/comparisonForConfiguration'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'configurationIdHex': configuration_id
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params))

    def best_n(self,
               n: int,
               field_name: str,
               goal: OptimizationGoal,
               kind: OptimizationKind,
               measurement_name: str = None,
               bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/best'
        params = AnalyticsUtil.append_influx_params({
            'n': n,
            'fieldName': field_name,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.get(url=url, params=params))

    def best_n_for_configuration(self,
                                 n: int,
                                 field_name: str,
                                 configuration_id: str,
                                 goal: OptimizationGoal,
                                 kind: OptimizationKind,
                                 measurement_name: str = None,
                                 bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/bestForConfiguration'
        params = AnalyticsUtil.append_influx_params({
            'n': n,
            'fieldName': field_name,
            'configurationIdHex': configuration_id,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.get(url=url, params=params))

    def avg_and_std_dev(self,
                        computation_ids: List[str],
                        field_name: str,
                        interval: int,
                        time_unit: TimeUnit,
                        measurement_name: str = None,
                        bucket_name: str = None) -> DataSeries:
        url = f'{self.base_url}/averageStddev'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'interval': interval,
            'unit': time_unit.name
        }, measurement_name, bucket_name)

        # todo: simplify code below?
        series = AnalyticsUtil.wrap_series_request(lambda: requests.post(
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

    def exceeding_thresholds(self,
                             field_name: str,
                             criteria_mode: CriteriaMode,
                             thresholds: Thresholds,
                             measurement_name: str = None,
                             bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/exceedingThresholds'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(
            url=url,
            params=params,
            json=thresholds.__dict__
        ))

    def exceeding_thresholds_for_configuration(self,
                                               field_name: str,
                                               configuration_id: str,
                                               criteria_mode: CriteriaMode,
                                               thresholds: Thresholds,
                                               measurement_name: str = None,
                                               bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/exceedingThresholdsForConfiguration'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
            'configurationIdHex': configuration_id,
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(
            url=url,
            params=params,
            json=thresholds.__dict__
        ))


class ConfigurationsAnalytics(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/statistics/configurations'

    def comparison(self,
                   configuration_ids: List[str],
                   field_name: str,
                   measurement_name: str = None,
                   bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/comparison'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params, json=configuration_ids))

    def comparison_for_experiment(self,
                                  experiment_name: str,
                                  field_name: str,
                                  measurement_name: str = None,
                                  bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/comparisonForExperiment'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'experimentName': experiment_name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params))

    def best_n(self,
               n: int,
               field_name: str,
               configuration_ids: List[str],
               goal: OptimizationGoal,
               kind: OptimizationKind,
               measurement_name: str = None,
               bucket_name: str = None):
        url = f'{self.base_url}/best'
        params = AnalyticsUtil.append_influx_params({
            'n': n,
            'fieldName': field_name,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params, json=configuration_ids))

    def best_n_for_experiment(self,
                              n: int,
                              field_name: str,
                              experiment_name: str,
                              goal: OptimizationGoal,
                              kind: OptimizationKind,
                              measurement_name: str = None,
                              bucket_name: str = None):
        url = f'{self.base_url}/bestForExperiment'
        params = AnalyticsUtil.append_influx_params({
            'n': n,
            'fieldName': field_name,
            'experimentName': experiment_name,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params))

    def exceeding_thresholds(self,
                             field_name: str,
                             criteria_mode: CriteriaMode,
                             thresholds: Thresholds,
                             configuration_ids: List[str],
                             measurement_name: str = None,
                             bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/exceedingThresholds'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(
            url=url,
            params=params,
            files={
                'thresholds': JSONUtil.multipart_payload(thresholds.__dict__),
                'configurationIdsHex': JSONUtil.multipart_payload(configuration_ids),
            }
        ))

    def exceeding_thresholds_for_experiment(self,
                                            field_name: str,
                                            experiment_name: str,
                                            criteria_mode: CriteriaMode,
                                            thresholds: Thresholds,
                                            measurement_name: str = None,
                                            bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/exceedingThresholdsForExperiment'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
            'experimentName': experiment_name,
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(
            url=url,
            params=params,
            files={
                'thresholds': JSONUtil.multipart_payload(thresholds.__dict__),
            }
        ))


class AnalyticsUtil:

    @staticmethod
    def wrap_series_request(request_lambda) -> List[DataSeries]:
        try:
            response = request_lambda()
            return AnalyticsUtil.wrap_response(response)
        except requests.RequestException as e:
            raise CloudberryConnectionException(e)

    @staticmethod
    def wrap_response(response: requests.Response) -> List[DataSeries]:
        if response.ok:
            return DataSeries.from_json_list(response.json())
        else:
            raise CloudberryException("Server request has failed", response.raise_for_status())

    @staticmethod
    def append_influx_params(params: dict,
                             measurement_name: str = None,
                             bucket_name: str = None) -> dict:
        if measurement_name is not None:
            params['measurementName'] = measurement_name
        if bucket_name is not None:
            params['bucketName'] = bucket_name
        return params
