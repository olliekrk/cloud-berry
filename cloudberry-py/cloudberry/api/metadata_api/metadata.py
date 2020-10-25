from typing import Optional

from .api_experiment import ExperimentApi
from .api_experiment_computation import ExperimentComputationApi
from .api_experiment_configuration import ExperimentConfigurationApi
from .. import CloudberryApi, CloudberryConfig


class MetaIds:
    def __init__(self,
                 computation_id,
                 configuration_id,
                 experiment_id):
        self.computation_id = computation_id
        self.configuration_id = configuration_id
        self.experiment_id = experiment_id


class Metadata(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/metadata'

    def experiment_api(self) -> ExperimentApi:
        return ExperimentApi(self.base_url)

    def experiment_configuration_api(self) -> ExperimentConfigurationApi:
        return ExperimentConfigurationApi(self.base_url)

    def experiment_computation_api(self) -> ExperimentComputationApi:
        return ExperimentComputationApi(self.base_url)

    def get_meta_ids(self, computation_id: str) -> Optional[MetaIds]:
        try:
            computation = self.experiment_computation_api().find_by_id(computation_id)
            configuration = self.experiment_configuration_api().find_by_computation(computation)
            experiment = self.experiment_api().find_by_configuration(configuration)
            return MetaIds(
                computation.computation_id_hex,
                configuration.experiment_configuration_id_hex,
                experiment.experiment_id_hex
            )
        except:
            return None
