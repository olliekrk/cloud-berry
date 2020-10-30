class CloudberryException(Exception):
    def __init__(self, message, cause) -> None:
        self.message = message
        self.cause = cause


class InvalidResponseException(CloudberryException):
    def __init__(self, cause) -> None:
        super().__init__("Cloudberry API failure", cause)


class CloudberryApiException(CloudberryException):
    def __init__(self, cause) -> None:
        super().__init__("Cloudberry API failure", cause)


class CloudberryServerException(CloudberryException):
    def __init__(self, cause) -> None:
        super().__init__("Cloudberry server failure (make sure connection is established)", cause)
