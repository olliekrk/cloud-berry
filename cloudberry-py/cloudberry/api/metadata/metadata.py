from .experiment import Experiment
from .experiment_computation import ExperimentComputation
from .experiment_configuration import ExperimentConfiguration
from ...api import CloudberryApi, CloudberryConfig


class Metadata(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/metadata'

    def experiment_api(self) -> Experiment:
        return Experiment(self.base_url)

    def experiment_configuration_api(self) -> ExperimentConfiguration:
        return ExperimentConfiguration(self.base_url)

    def experiment_computation_api(self) -> ExperimentComputation:
        return ExperimentComputation(self.base_url)
