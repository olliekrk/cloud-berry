from ..str_generator import auto_str
from typing import List


@auto_str
class Experiment:
    def __init__(self,
                 experiment_id_hex,
                 name,
                 parameters,
                 time):
        self.experiment_id_hex = experiment_id_hex
        self.name = name
        self.parameters = parameters
        self.time = time

    @staticmethod
    def from_json(json_dict: dict):
        return Experiment(
            json_dict['id'],
            json_dict['name'],
            json_dict['parameters'],
            json_dict['time'],
        )
