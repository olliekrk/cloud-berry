from typing import List

import matplotlib.pyplot as plt

from .model import DataSeries

DEFAULT_PLOT_SIZE = (15, 5)
DEFAULT_PLOT_COLOR = 'red'
ERROR_COLOR_RGB = (1, 0, 0)


class DataSeriesPlots:
    def __init__(self, series: DataSeries) -> None:
        self.series = series

    def show(self,
             x_field: str,
             y_field: str,
             yerr_field: str = None,
             figsize=DEFAULT_PLOT_SIZE,
             title: str = None) -> plt.Axes:
        df = self.series.as_data_frame
        figure, axes = plt.subplots(figsize=figsize)
        if title:
            axes.set_title(title)

        y_errors = None if yerr_field is None or yerr_field not in df else df[yerr_field]
        axes.errorbar(x=df[x_field],
                      y=df[y_field],
                      yerr=y_errors,
                      label=self.series.series_name,
                      ecolor=ERROR_COLOR_RGB,
                      color=DataSeriesPlots._get_series_color(self.series))

        plt.show()
        return axes

    @classmethod
    def compare(cls,
                series: List[DataSeries],
                x_field: str,
                y_field: str,
                yerr_field: str = None,
                figsize=DEFAULT_PLOT_SIZE,
                title=None) -> plt.Axes:
        figure, axes = plt.subplots(figsize=figsize)
        if title:
            axes.set_title(title)
        for ds in series:
            df = ds.as_data_frame
            y_errors = None if yerr_field is None or yerr_field not in df else df[yerr_field]

            axes.errorbar(x=df[x_field],
                          y=df[y_field],
                          yerr=y_errors,
                          label=ds.series_name,
                          ecolor=ERROR_COLOR_RGB,
                          color=DataSeriesPlots._get_series_color(ds))
        axes.legend()
        return axes

    @staticmethod
    def _get_series_color(series: DataSeries):
        max_hex = 256

        def rgb_scaled(v):
            return min(float(v % max_hex) / (max_hex - 1), 1)

        series_name_hash: int = hash(series.series_name) % max_hex ** 3
        red = rgb_scaled(series_name_hash)
        green = rgb_scaled(series_name_hash / max_hex)
        blue = rgb_scaled(series_name_hash / max_hex ** 2)
        return red, green, blue
