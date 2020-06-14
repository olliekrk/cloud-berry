class CloudberryConfig:
    cb_url = None
    cb_port = None

    def __init__(self, url, port):
        self.configure(url, port)

    def configure(self, url, port):
        self.cb_url = url
        self.cb_port = port

    def get_base_url(self):
        return f'{self.cb_url}:{self.cb_port}'
