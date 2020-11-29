import requests

from .backend import CloudberryApi, CloudberryConfig
from .backend.exceptions import *
from .constants import *
from .data import DataFilters
from .json_util import JSONUtil
from .model import OptimizationGoal, OptimizationKind, CriteriaMode, Thresholds, ThresholdsType, DataSeriesPack
from .model.metadata import *
from .model.util import SeriesApiParams


class ComputationSeries(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/series/computations'

    def for_computations(self,
                         computations: List[ExperimentComputation],
                         field_name: str,
                         api_params: SeriesApiParams = None,
                         data_filters: DataFilters = None) -> DataSeriesPack:
        url = f'{self.base_url}/comparison'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name
        }, api_params)
        ids = get_ids_for_computations(computations)
        json = {
            'ids': ids,
        }
        if data_filters is not None:
            json['filters'] = data_filters.make_dto()
        return AnalyticsUtil.unpack(
            lambda: requests.post(url=url, params=params, json=json)
        )

    def for_configuration(self,
                          configuration: ExperimentConfiguration,
                          field_name: str,
                          api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/comparisonForConfiguration'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params))

    def best_n(self,
               n: int,
               field_name: str,
               goal: OptimizationGoal,
               kind: OptimizationKind,
               api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/best'
        params = AnalyticsUtil.with_api_params({
            'n': n,
            'fieldName': field_name,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params))

    def best_n_for_configuration(self,
                                 n: int,
                                 field_name: str,
                                 configuration: ExperimentConfiguration,
                                 goal: OptimizationGoal,
                                 kind: OptimizationKind,
                                 api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/bestForConfiguration'
        params = AnalyticsUtil.with_api_params({
            'n': n,
            'fieldName': field_name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params))

    def exceeding_thresholds(self,
                             field_name: str,
                             criteria_mode: CriteriaMode,
                             thresholds: Thresholds,
                             api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/exceedingThresholds'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params, json=thresholds.json()))

    def exceeding_thresholds_for_configuration(self,
                                               field_name: str,
                                               configuration: ExperimentConfiguration,
                                               criteria_mode: CriteriaMode,
                                               thresholds: Thresholds,
                                               api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/exceedingThresholdsForConfiguration'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex,
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params, json=thresholds.json()))

    def exceeding_thresholds_relatively(self,
                                        field_name: str,
                                        configuration: ExperimentConfiguration,
                                        criteria_mode: CriteriaMode,
                                        thresholds: Thresholds,
                                        thresholds_type: ThresholdsType,
                                        api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/exceedingThresholdsRelatively'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
            'thresholdsType': thresholds_type.name,
            CONFIGURATION_ID_HEX: configuration.experiment_configuration_id_hex,
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params, json=thresholds.json()))


class ConfigurationSeries(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/series/configurations'

    def for_configurations(self,
                           configurations: List[ExperimentConfiguration],
                           field_name: str,
                           api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/comparison'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name
        }, api_params)
        return AnalyticsUtil.unpack(
            lambda: requests.post(url=url, params=params, json=get_ids_for_configurations(configurations))
        )

    def for_experiment(self,
                       experiment_name: str,
                       field_name: str,
                       api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/comparisonForExperiment'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name,
            'experimentName': experiment_name
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params))

    def best_n(self,
               n: int,
               field_name: str,
               configurations: List[ExperimentConfiguration],
               goal: OptimizationGoal,
               kind: OptimizationKind,
               api_params: SeriesApiParams = None):
        url = f'{self.base_url}/best'
        params = AnalyticsUtil.with_api_params({
            'n': n,
            'fieldName': field_name,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, api_params)
        return AnalyticsUtil.unpack(
            lambda: requests.post(url=url, params=params, json=get_ids_for_configurations(configurations))
        )

    def best_n_for_experiment(self,
                              n: int,
                              field_name: str,
                              experiment_name: str,
                              goal: OptimizationGoal,
                              kind: OptimizationKind,
                              api_params: SeriesApiParams = None):
        url = f'{self.base_url}/bestForExperiment'
        params = AnalyticsUtil.with_api_params({
            'n': n,
            'fieldName': field_name,
            'experimentName': experiment_name,
            'optimizationGoal': goal.name,
            'optimizationKind': kind.name
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(url=url, params=params))

    def exceeding_thresholds(self,
                             field_name: str,
                             criteria_mode: CriteriaMode,
                             thresholds: Thresholds,
                             configurations: List[ExperimentConfiguration],
                             api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/exceedingThresholds'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(
            url=url,
            params=params,
            files={
                'thresholds': JSONUtil.multipart_payload(thresholds.json()),
                'configurationIdsHex': JSONUtil.multipart_payload(get_ids_for_configurations(configurations)),
            }
        ))

    def exceeding_thresholds_for_experiment(self,
                                            field_name: str,
                                            experiment_name: str,
                                            criteria_mode: CriteriaMode,
                                            thresholds: Thresholds,
                                            api_params: SeriesApiParams = None) -> DataSeriesPack:
        url = f'{self.base_url}/exceedingThresholdsForExperiment'
        params = AnalyticsUtil.with_api_params({
            'fieldName': field_name,
            'mode': criteria_mode.name,
            'experimentName': experiment_name,
        }, api_params)
        return AnalyticsUtil.unpack(lambda: requests.post(
            url=url,
            params=params,
            files={
                'thresholds': JSONUtil.multipart_payload(thresholds.json()),
            }
        ))


class AnalyticsUtil:

    @staticmethod
    def unpack(request_lambda) -> DataSeriesPack:
        try:
            response = request_lambda()
            if response.ok:
                return DataSeriesPack.from_json(response.json())
            else:
                raise CloudberryApiException(response.raise_for_status())
        except ValueError as e:
            raise InvalidResponseException(e)
        except requests.RequestException as e:
            raise CloudberryServerException(e)

    @staticmethod
    def with_api_params(params: dict, api_params: SeriesApiParams = None):
        if api_params is not None:
            if api_params.bucket_name is not None:
                params['bucketName'] = api_params.bucket_name
            if api_params.measurement_name is not None:
                params['measurementName'] = api_params.measurement_name
        return params
