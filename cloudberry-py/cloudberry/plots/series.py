from pandas import DataFrame


class PlotSeries:
    def __init__(self,
                 name: str,
                 data: DataFrame,
                 x_field: str,
                 y_field: str,
                 y_err_field: str = None):
        self.name = name
        self.data = data
        self.x_field = x_field
        self.y_field = y_field
        self.y_err_field = y_err_field
