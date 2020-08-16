from typing import List

import matplotlib.pyplot as plt

from .wrappers import DataSeries

DEFAULT_PLOT_SIZE = (15, 5)
DEFAULT_PLOT_KIND = 'scatter'
DEFAULT_PLOT_COLOR = 'red'


class DataSeriesPlots:
    def __init__(self, series: DataSeries) -> None:
        self.series = series

    def show(self,
             x_field: str,
             y_field: str,
             figsize=DEFAULT_PLOT_SIZE,
             kind=DEFAULT_PLOT_KIND,
             title=None) -> plt.Axes:
        df = self.series.as_data_frame
        axes = df.plot(x=x_field,
                       y=y_field,
                       color=DataSeriesPlots._get_series_color(self.series),
                       title=title,
                       figsize=figsize,
                       kind=kind)
        plt.show()
        return axes

    @classmethod
    def compare(cls,
                series: List[DataSeries],
                x_field: str,
                y_field: str,
                figsize=DEFAULT_PLOT_SIZE,
                kind=DEFAULT_PLOT_KIND,
                title=None) -> plt.Axes:
        figure, axes = plt.subplots(figsize=figsize)
        if title:
            axes.set_title(title)
        for ds in series:
            df = ds.as_data_frame
            axes.plot(df[x_field],
                      df[y_field],
                      label=ds.series_name,
                      color=DataSeriesPlots._get_series_color(ds))
        axes.legend()
        return axes

    @staticmethod
    def _get_series_color(series: DataSeries):
        max_hex = 256

        def rgb_scaled(v):
            return float(v % max_hex) / (max_hex - 1)

        series_name_hash: int = hash(series.series_name) % max_hex ** 3
        red = rgb_scaled(series_name_hash)
        green = rgb_scaled(series_name_hash / max_hex)
        blue = rgb_scaled(series_name_hash / max_hex ** 2)
        return red, green, blue
