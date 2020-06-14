import requests

from .config import CloudberryConfig


class FileUploader:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config

    def upload_file(self, file_name):
        with open(file_name, 'rb') as file:
            url = f'{self.config.get_base_url()}/logs/upload'
            r = requests.post(url, files={'file': file})
            return r.json()
