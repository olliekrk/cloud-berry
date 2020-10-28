import requests

from .backend import CloudberryApi, CloudberryConfig, CloudberryException, CloudberryConnectionException
from .constants import *
from .json_util import JSONUtil
from .model import DataSeries, OptimizationGoal, OptimizationKind, CriteriaMode, Thresholds, ThresholdsType
from .model.metadata import *


class ComputationSeries(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/series/computations'

    def comparison(self,
                   computations: List[ExperimentComputation],
                   field_name: str,
                   measurement_name: str = None,
                   bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/comparison'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params,
                                                                       json=get_ids_for_computations(computations)))

    def comparison_for_configuration(self,
                                     configuration: ExperimentConfiguration,
                                     field_name: str,
                                     measurement_name: str = None,
                                     bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/comparisonForConfiguration'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex
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
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params))

    def best_n_for_configuration(self,
                                 n: int,
                                 field_name: str,
                                 configuration: ExperimentConfiguration,
                                 goal: OptimizationGoal,
                                 kind: OptimizationKind,
                                 measurement_name: str = None,
                                 bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/bestForConfiguration'
        params = AnalyticsUtil.append_influx_params({
            'n': n,
            'fieldName': field_name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(url=url, params=params))

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
                                               configuration: ExperimentConfiguration,
                                               criteria_mode: CriteriaMode,
                                               thresholds: Thresholds,
                                               measurement_name: str = None,
                                               bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/exceedingThresholdsForConfiguration'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex,
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(
            url=url,
            params=params,
            json=thresholds.__dict__
        ))

    def exceeding_thresholds_relatively(self,
                                        field_name: str,
                                        configuration: ExperimentConfiguration,
                                        criteria_mode: CriteriaMode,
                                        thresholds: Thresholds,
                                        thresholds_type: ThresholdsType,
                                        measurement_name: str = None,
                                        bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/exceedingThresholdsRelatively'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
            'thresholdsType': thresholds_type.name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex,
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(lambda: requests.post(
            url=url,
            params=params,
            json=thresholds.__dict__
        ))


class ConfigurationSeries(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/series/configurations'

    def comparison(self,
                   configurations: List[ExperimentConfiguration],
                   field_name: str,
                   measurement_name: str = None,
                   bucket_name: str = None) -> List[DataSeries]:
        url = f'{self.base_url}/comparison'
        params = AnalyticsUtil.append_influx_params({
            'fieldName': field_name
        }, measurement_name, bucket_name)
        return AnalyticsUtil.wrap_series_request(
            lambda: requests.post(url=url, params=params, json=get_ids_for_configurations(configurations)))

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
               configurations: List[ExperimentConfiguration],
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
        return AnalyticsUtil.wrap_series_request(
            lambda: requests.post(url=url, params=params, json=get_ids_for_configurations(configurations)))

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
                             configurations: List[ExperimentConfiguration],
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
                'configurationIdsHex': JSONUtil.multipart_payload(get_ids_for_configurations(configurations)),
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
