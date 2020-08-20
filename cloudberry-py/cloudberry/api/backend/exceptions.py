class CloudberryException(Exception):
    def __init__(self, message, cause) -> None:
        self.message = message
        self.cause = cause


class CloudberryConnectionException(CloudberryException):
    def __init__(self, cause) -> None:
        super().__init__("Connection could not be established", cause)
