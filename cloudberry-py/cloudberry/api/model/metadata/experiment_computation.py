from typing import List

from ..str_generator import auto_str


@auto_str
class ExperimentComputation:
    def __init__(self,
                 computation_id_hex,
                 configuration_id,
                 time):
        self.computation_id_hex = computation_id_hex
        self.configuration_id = configuration_id
        self.time = time

    @staticmethod
    def from_json(json_dict: dict):
        return ExperimentComputation(
            json_dict['id'],
            json_dict['configurationId'],
            json_dict['time'],
        )


def get_ids_for_computations(computations: List[ExperimentComputation]):
    return list(map(lambda computation: computation.computation_id_hex, computations))
