from .config import CloudberryConfig
import requests


class LogsApi:

    def __init__(self, config: CloudberryConfig) -> None:
        self.config = config
        self.params = dict()

    def workplace(self):
        self.params['prefix'] = 'workplace'
        return self

    def by_evaluation_id(self, evaluation_id):
        self.params['evaluation_id'] = evaluation_id
        return self

    def by_workplace_id(self, workplace_id):
        self.params['workplace_id'] = workplace_id
        return self

    def get_raw(self):
        prefix = self.params.get('prefix')
        evaluation_id = self.params.get('evaluation_id')
        workplace_id = self.params.get('workplace_id')

        def segment(part):
            return f'/{part}' if part is not None else ''

        base_url = self.config.get_base_url()
        url = f'{base_url}/raw{segment(prefix)}{segment(evaluation_id)}{segment(workplace_id)}'
        r = requests.get(url)
        return r.json()

    def get_dataframe(self):
        pass

    def get_csv(self):
        pass
