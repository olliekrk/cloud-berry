class ComputationData:
    def __init__(self, time, fields, tags) -> None:
        self.time = time
        self.fields = fields
        self.tags = tags


class LogFilters:
    def __init__(self, tags: dict, fields: dict) -> None:
        self.tags = tags
        self.fields = fields
