from typing import List

import matplotlib.pyplot as plt
from colorhash import ColorHash

from .model import DataSeries

DEFAULT_PLOT_SIZE = (15, 5)
DEFAULT_PLOT_COLOR = 'red'
ERROR_COLOR_RGB = (1, 0, 0)
ERROR_LINE_WIDTH = .5


# fixme: deprecated in favour of cloudberry.plots

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
                      elinewidth=ERROR_LINE_WIDTH,
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

            def safe_get_field(field: str):
                if field in df.columns:
                    return df[field]
                else:
                    return []

            axes.errorbar(x=safe_get_field(x_field),
                          y=safe_get_field(y_field),
                          yerr=y_errors,
                          label=ds.series_name,
                          ecolor=ERROR_COLOR_RGB,
                          elinewidth=ERROR_LINE_WIDTH,
                          color=DataSeriesPlots._get_series_color(ds))
        axes.legend()
        return axes

    @staticmethod
    def _get_series_color(series: DataSeries):
        color = ColorHash(series.series_name)
        return tuple(map(lambda c: c / 255.0, color.rgb))
