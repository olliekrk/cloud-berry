from pandas import DataFrame


class PlotSeries:
    def __init__(self, name: str, data: DataFrame):
        self.name = name
        self.data = data
