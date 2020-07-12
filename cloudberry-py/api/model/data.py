class ComputationData:
    def __init__(self, time, fields, tags) -> None:
        self.time = time
        self.fields = fields
        self.tags = tags


class LogFilters:
    def __init__(self, tags: dict, fields: dict) -> None:
        self.tags = tags
        self.fields = fields


class ImportDetails:
    def __init__(self, headers_keys: dict, headers_measurements: dict) -> None:
        self.headers_keys = headers_keys
        self.headers_measurements = headers_measurements
