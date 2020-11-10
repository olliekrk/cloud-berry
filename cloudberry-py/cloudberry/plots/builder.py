from typing import Dict

from cloudberry.api.model import SeriesInfo
from .exceptions import DuplicatedSeriesName, InvalidPlotsConfiguration, DuplicatedTrendLine
from .flavour_plotly import PlotlyFlavourPlot
from .flavours import PlottingFlavour
from .properties import PlotProperties
from .series import PlotSeries, PlotSeriesPack
from .trendlines import TrendLine


class PlotBuilder:

    def __init__(self, properties: PlotProperties):
        self.properties = properties
        self.__series: Dict[str, PlotSeries] = {}
        self.__avg_series: Dict[str, PlotSeries] = {}
        self.__trends: Dict[str, TrendLine] = {}

    def add_series(self, series: PlotSeries, replace: bool = True):
        PlotBuilder.__add_series(series, self.__series, replace)

    def add_avg_series(self, avg_series: PlotSeries, replace: bool = True):
        PlotBuilder.__add_series(avg_series, self.__avg_series, replace)

    def add_trend(self, trend_name: str, trend: TrendLine, replace: bool = True):
        if not replace and trend_name in self.__trends:
            raise DuplicatedTrendLine(f"{trend_name} is already defined")
        else:
            self.__trends[trend_name] = trend

    def add_pack(self, pack: PlotSeriesPack):
        for s in pack.series:
            self.add_series(s)
        for a in pack.averages:
            if a is not None:
                self.add_avg_series(a)

    def delete_series(self, series_info: SeriesInfo):
        PlotBuilder.__delete_series(series_info, self.__series)

    def delete_avg_series(self, series_info: SeriesInfo):
        PlotBuilder.__delete_series(series_info, self.__avg_series)

    def delete_trend(self, name: str):
        if name in self.__trends:
            del self.__trends[name]

    def get_series(self):
        """Return a shallow copy of series currently stored in builder"""
        return dict(self.__series)

    def get_avg_series(self):
        return dict(self.__avg_series)

    def get_trends(self):
        return dict(self.__trends)

    def plot(self):
        flavour = self.properties.flavour
        if flavour == PlottingFlavour.Plotly:
            return PlotlyFlavourPlot(
                self.get_series(),
                self.get_avg_series(),
                self.get_trends(),
                self.properties
            ).plot()
        else:
            raise InvalidPlotsConfiguration(f"Plot builder not implemented for flavour: {flavour.name}")

    @staticmethod
    def __delete_series(series_info: SeriesInfo, all_series: Dict[str, PlotSeries]):
        if series_info in all_series:
            del all_series[series_info.series_id]

    @staticmethod
    def __add_series(series: PlotSeries,
                     all_series: Dict[str, PlotSeries],
                     replace: bool):
        if not replace and series.series_info in all_series:
            raise DuplicatedSeriesName(f"{series.series_info} is already defined")
        else:
            all_series[series.series_info.series_id] = series
