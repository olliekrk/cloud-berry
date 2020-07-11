class ComputationData:
    def __init__(self, time, fields, tags) -> None:
        self.time = time
        self.fields = fields
        self.tags = tags


class DataSeries:
    def __init__(self, seriesName: str, data: list) -> None:
        self.seriesName = seriesName
        self.data = data


class LogFilters:
    def __init__(self, tags: dict, fields: dict) -> None:
        self.tags = tags
        self.fields = fields
