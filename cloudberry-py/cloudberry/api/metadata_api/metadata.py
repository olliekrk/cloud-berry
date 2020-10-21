from .api_experiment import ExperimentApi
from .api_experiment_computation import ExperimentComputationApi
from .api_experiment_configuration import ExperimentConfigurationApi
from ...api import CloudberryApi, CloudberryConfig


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
