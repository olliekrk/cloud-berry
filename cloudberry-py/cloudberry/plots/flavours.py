from enum import Enum
from typing import Dict

from .properties import PlotProperties
from .series import PlotSeries


class PlottingFlavour(Enum):
    Plotly = 1  # https://plotly.com/python/
    Matplotlib = 2  # https://matplotlib.org/
    Seaborn = 3  # https://seaborn.pydata.org/tutorial/axis_grids.html


class PlotlyFlavourPlot:
    def __init__(self,
                 series: Dict[str, PlotSeries],
                 trend_series: Dict[str, PlotSeries],
                 avg_series: Dict[str, PlotSeries],
                 properties: PlotProperties):
        self.series = series,
        self.trends = trend_series,
        self.averages = avg_series,
        self.properties = properties

    def plot(self):
        pass
