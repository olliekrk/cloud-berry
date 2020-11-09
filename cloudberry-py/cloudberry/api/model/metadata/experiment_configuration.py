from typing import List

from ..str_generator import auto_str


@auto_str
class ExperimentConfiguration:
    def __init__(self,
                 experiment_configuration_id_hex,
                 experiment_id_hex,
                 configuration_name,
                 parameters,
                 time):
        self.experiment_configuration_id_hex = experiment_configuration_id_hex
        self.experiment_id_hex = experiment_id_hex
        self.configuration_name = configuration_name
        self.parameters = parameters
        self.time = time

    @staticmethod
    def from_json(json_dict: dict):
        return ExperimentConfiguration(
            json_dict['id'],
            json_dict['experimentId'],
            json_dict['configurationName'],
            json_dict['parameters'],
            json_dict['time'],
        )


def get_ids_for_configurations(configurations: List[ExperimentConfiguration]):
    return list(map(lambda configuration: configuration.experiment_configuration_id_hex, configurations))
