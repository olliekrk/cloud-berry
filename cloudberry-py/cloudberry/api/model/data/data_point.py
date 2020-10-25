from ..metadata import ExperimentComputation


class DataPoint:
    def __init__(self, time, fields, tags, computation: ExperimentComputation = None) -> None:
        self.time = time
        self.fields = fields
        self.tags = tags
        if computation is not None:
            tags['computationId'] = computation.computation_id_hex
