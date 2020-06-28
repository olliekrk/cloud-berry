import pandas as pd
import requests

from .config import CloudberryConfig

#  TODO: Python, API, test, modyfikacja tak zeby dzialalo bo sie teraz nie zadziala nic

class LogsApi:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config
        self.params = dict()


    def get_raw(self):

        def segment(part):
            return f'/{part}' if part is not None else ''

        base_url = self.config.get_base_url()
        url = f'{base_url}/raw{segment(prefix)}{segment(evaluation_id)}{segment(workplace_id)}'
        r = requests.get(url)
        return r.json()

    def get_dataframe(self):
        raw_json = self.get_raw()
        return pd.DataFrame(raw_json)

    def get_csv(self):
        pass
