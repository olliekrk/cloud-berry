from enum import Enum
from typing import Dict

import numpy as np
import plotly.graph_objects as pgo
from sklearn.linear_model import LinearRegression
from sklearn.neighbors import KNeighborsRegressor

from .exceptions import InvalidTrendLine
from .properties import PlotProperties, PlotSeriesKind
from .series import PlotSeries
from .trendlines import TrendLine
from .utils import PlotUtils


class PlotlyTrendLineKind(Enum):
    LINEAR = 'linear'
    KNN10 = 'knn'


class PlotlyTrendLine(TrendLine):
    def __init__(self,
                 related_series_name: str,
                 kind: PlotlyTrendLineKind):
        self.related_series_name = related_series_name
        self.kind = kind


class PlotlyFlavourPlot:
    def __init__(self,
                 series: Dict[str, PlotSeries],
                 avg_series: Dict[str, PlotSeries],
                 trends: Dict[str, TrendLine],
                 properties: PlotProperties):
        self.series = series
        self.trends = trends
        self.averages = avg_series
        self.properties = properties

    def plot(self) -> pgo.Figure:
        fig = pgo.Figure()

        if self.properties.show_series:
            for s in self.series.values():
                self.__add_trace(s, fig)

        if self.properties.show_averages:
            for s in self.averages.values():
                self.__add_trace(s, fig)

        if self.properties.show_trends:
            for (trend_name, trend) in self.trends.items():
                self.__add_trend_trace(trend_name, trend, fig)

        self.__update_layout(fig)
        self.__update_axes(fig)

        return fig

    def __add_trace(self, series: PlotSeries, fig: pgo.Figure):
        trace_mode = 'markers' if self.properties.default_series_kind == PlotSeriesKind.SCATTER else 'lines+markers'
        trace = pgo.Scatter(
            name=series.name,
            mode=trace_mode,
            x=series.data[series.x_field],
            y=series.data[series.y_field],
            error_y=None if series.y_err_field is None else {
                'type': 'data',
                'visible': series.y_err_field is not None,
                'array': series.data[series.y_err_field],
                'color': 'red',
            },
            marker={
                'symbol': 'circle',
            },
            line={
                'color': PlotUtils.series_color_hex(series.name),
            }
        )
        fig.add_trace(trace)

    def __update_axes(self, fig: pgo.Figure):
        if self.properties.x_axis_name is not None:
            fig.update_xaxes(
                patch={
                    'visible': True,
                    'title': {
                        'text': self.properties.x_axis_name,
                    }
                },
                overwrite=True,
            )
        if self.properties.y_axis_name is not None:
            fig.update_yaxes(
                patch={
                    'visible': True,
                    'title': {
                        'text': self.properties.y_axis_name,
                    }
                },
                overwrite=True,
            )

    def __update_layout(self, fig: pgo.Figure):
        fig.update_layout(
            dict1={
                'title': {
                    'text': self.properties.title,
                },
                'showlegend': self.properties.show_legend,
            },
            overwrite=True
        )

    def __add_trend_trace(self, trend_name: str, trend: PlotlyTrendLine, fig: pgo.Figure):
        def reshape(d):
            return d.reshape(-1, 1)

        def reshape_back(d):
            return d.reshape(1, -1)[0]

        if trend.related_series_name not in self.series:
            raise InvalidTrendLine(f"Missing related series: {trend.related_series_name}")

        series = self.series[trend.related_series_name]
        xs_base = np.array(series.data[series.x_field])
        xs = reshape(xs_base)
        ys = reshape(np.array(series.data[series.y_field]))
        model = None  # https://plotly.com/python/ml-regression/

        if trend.kind == PlotlyTrendLineKind.LINEAR:
            model = PlotlyFlavourPlot.linear_regression_model()
        elif trend.kind == PlotlyTrendLineKind.KNN10:
            model = PlotlyFlavourPlot.knn10_regression_model(xs_base.size)
        else:
            raise InvalidTrendLine(f"Other kind of trend line than linear is not implemented")

        model.fit(xs, ys)
        xs_predict = reshape(np.linspace(xs.min(), xs.max(), 100))
        ys_predict = model.predict(xs_predict)

        trace = pgo.Scatter(
            x=reshape_back(xs_predict),
            y=reshape_back(ys_predict),
            name=trend_name,
            mode='lines',
            line={
                'dash': 'dash',
                'color': 'red',
            }
        )

        fig.add_trace(trace)

    @staticmethod
    def knn10_regression_model(max_n: int):
        return KNeighborsRegressor(min(10, max_n), weights='distance')

    @staticmethod
    def linear_regression_model():
        return LinearRegression()
