from typing import Dict

from .exceptions import DuplicatedSeriesName, InvalidPlotsConfiguration
from .flavour_plotly import PlotlyFlavourPlot
from .flavours import PlottingFlavour
from .properties import PlotProperties
from .series import PlotSeries


class PlotBuilder:
    __series: Dict[str, PlotSeries] = {}
    __trend_series: Dict[str, PlotSeries] = {}
    __avg_series: Dict[str, PlotSeries] = {}

    def __init__(self, properties: PlotProperties):
        self.properties = properties

    def add_series(self, series: PlotSeries, replace: bool = True):
        PlotBuilder.__add_series(series, self.__series, replace)

    def add_avg_series(self, avg_series: PlotSeries, replace: bool = True):
        PlotBuilder.__add_series(avg_series, self.__avg_series, replace)

    def add_trend_line(self, trend_series: PlotSeries, replace: bool = True):
        PlotBuilder.__add_series(trend_series, self.__trend_series, replace)

    def delete_series(self, name: str):
        PlotBuilder.__delete_series(name, self.__series)

    def delete_avg_series(self, name: str):
        PlotBuilder.__delete_series(name, self.__avg_series)

    def delete_trend_series(self, name: str):
        PlotBuilder.__delete_series(name, self.__trend_series)

    def get_series(self):
        """Return a shallow copy of series currently stored in builder"""
        return dict(self.__series)

    def get_avg_series(self):
        return dict(self.__avg_series)

    def get_trend_series(self):
        return dict(self.__trend_series)

    def plot(self):
        flavour = self.properties.flavour
        if flavour == PlottingFlavour.Plotly:
            return PlotlyFlavourPlot(
                self.get_series(),
                self.get_avg_series(),
                self.get_trend_series(),
                self.properties
            ).plot()
        else:
            raise InvalidPlotsConfiguration(f"Plot builder not implemented for flavour: {flavour.name}")

    @staticmethod
    def __delete_series(series_name: str, all_series: Dict[str, PlotSeries]):
        if series_name in all_series:
            del all_series[series_name]

    @staticmethod
    def __add_series(series: PlotSeries,
                     all_series: Dict[str, PlotSeries],
                     replace: bool):
        if not replace and series.name in all_series:
            raise DuplicatedSeriesName(f"{series.name} is already defined")
        else:
            all_series[series.name] = series
