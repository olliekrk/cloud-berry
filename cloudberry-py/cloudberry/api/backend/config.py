class CloudberryConfig:
    cb_url = None

    def __init__(self, url):
        self.reconfigure(url)

    def reconfigure(self, url):
        self.cb_url = url

    def base_url(self):
        return self.cb_url


class CloudberryApi:
    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config
