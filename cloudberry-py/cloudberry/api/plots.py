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
             color=DEFAULT_PLOT_COLOR,
             title=None) -> plt.Axes:
        df = self.series.as_data_frame
        axes = df.plot(x=x_field, y=y_field, color=color, title=title, figsize=figsize, kind=kind)
        plt.show()
        return axes

    @classmethod
    def compare(cls,
                series: List[DataSeries],
                x_field: str,
                y_field: str,
                figsize=DEFAULT_PLOT_SIZE,
                kind=DEFAULT_PLOT_KIND,
                color=DEFAULT_PLOT_COLOR,
                title=None) -> plt.Axes:
        figure, axes = plt.subplots(figsize=figsize)
        if title:
            axes.set_title(title)
        for ds in series:
            df = ds.as_data_frame
            axes.plot(df[x_field], df[y_field], label=ds.series_name)
        axes.legend()
        return axes
