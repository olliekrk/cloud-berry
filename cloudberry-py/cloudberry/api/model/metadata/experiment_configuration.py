from ..str_generator import auto_str


@auto_str
class ExperimentConfiguration:
    def __init__(self,
                 experiment_configuration_id_hex,
                 experiment_id_hex,
                 configuration_file_name,
                 parameters,
                 time):
        self.experiment_configuration_id_hex = experiment_configuration_id_hex
        self.experiment_id_hex = experiment_id_hex
        self.configuration_file_name = configuration_file_name
        self.parameters = parameters
        self.time = time

    @staticmethod
    def from_json(json_dict: dict):
        return ExperimentConfiguration(
            json_dict['id'],
            json_dict['experimentId'],
            json_dict['configurationFileName'],
            json_dict['parameters'],
            json_dict['time'],
        )
