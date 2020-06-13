# import requests

class CloudberryConnector:
    cb_url = None
    cb_port = None

    def connect(self, url, port):
        self.cb_url = url
        self.cb_port = port
