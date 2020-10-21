from .str_generator import auto_str


@auto_str
class AnomalyReport:
    def __init__(self,
                 computation_id,
                 stddev,
                 mean,
                 spread,
                 minimum,
                 maximum,
                 max_diff):
        self.computation_id = computation_id
        self.stddev = stddev
        self.mean = mean
        self.spread = spread
        self.minimum = minimum
        self.maximum = maximum
        self.max_diff = max_diff

    @staticmethod
    def from_json(json_dict: dict):
        return AnomalyReport(
            json_dict['computationId'],
            json_dict['stddev'],
            json_dict['mean'],
            json_dict['spread'],
            json_dict['min'],
            json_dict['max'],
            json_dict['maxDiff']
        )
