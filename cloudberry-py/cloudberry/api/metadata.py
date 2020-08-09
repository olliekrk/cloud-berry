from cloudberry import CloudberryConfig
from cloudberry.api.config import CloudberryApi


class Metadata(CloudberryApi):
    def __init__(self, config: CloudberryConfig) -> None:
        super().__init__(config)
