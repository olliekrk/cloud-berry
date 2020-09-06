from .experiment import Experiment
from ...api import CloudberryApi, CloudberryConfig


class Metadata(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
        self.base_url = f'{config.base_url()}/metadata'

    def experiment_api(self) -> Experiment:
        return Experiment(self.base_url)
