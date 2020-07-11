class CloudberryConfig:
    cb_url = None

    def __init__(self, url):
        self.configure(url)

    def configure(self, url):
        self.cb_url = url

    def get_base_url(self):
        return self.cb_url
