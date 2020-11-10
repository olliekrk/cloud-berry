from enum import Enum
from typing import Dict
from uuid import uuid4

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
    CONST = 'const'


class PlotlyTrendLine(TrendLine):
    def __init__(self,
                 related_series_id: str,
                 kind: PlotlyTrendLineKind,
                 constant: float = None):
        self.related_series_id = related_series_id
        self.kind = kind
        self.constant = constant


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
        show_error_y = self.properties.show_error_bars and \
                       series.y_err_field is not None and \
                       series.y_err_field in series.data.columns
        trace_mode = {
            PlotSeriesKind.SCATTER: 'markers',
            PlotSeriesKind.SCATTERLINE: 'markers+lines',
            PlotSeriesKind.LINE: 'lines',
        }[self.properties.default_series_kind]
        trace = pgo.Scatter(
            name=series.series_info.series_name,
            mode=trace_mode,
            x=series.data[series.x_field],
            y=series.data[series.y_field],
            error_y=None if not show_error_y else {
                'type': 'data',
                'visible': series.y_err_field is not None,
                'array': series.data[series.y_err_field],
                'color': PlotUtils.series_color_hex(series.series_info.series_id),
                'thickness': 0.5,
                'width': 1,
            },
            marker={
                'symbol': 'circle',
            },
            line={
                'color': PlotUtils.series_color_hex(series.series_info.series_id),
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
                'title': None if not self.properties.show_title else {
                    'text': self.properties.title,
                },
                'showlegend': self.properties.show_legend,
                'margin': {
                    'l': 60,
                    'r': 30,
                    't': 30,
                    'b': 60,
                }
            },
            overwrite=True
        )

    def __add_trend_trace(self, trend_name: str, trend: PlotlyTrendLine, fig: pgo.Figure):
        if trend.kind == PlotlyTrendLineKind.CONST:
            self.__add_const_trend_trace(trend_name, trend, fig)
        elif trend.kind == PlotlyTrendLineKind.LINEAR:
            self.__add_ml_linear_trend_trace(trend_name, trend, fig)
        elif trend.kind == PlotlyTrendLineKind.KNN10:
            self.__add_ml_knn_trend_trace(trend_name, trend, fig)

    def __add_ml_knn_trend_trace(self, trend_name: str, trend: PlotlyTrendLine, fig: pgo.Figure):
        all_series = {**self.series, **self.averages}
        if trend.related_series_id not in all_series:
            raise InvalidTrendLine(f"Missing related series: {trend.related_series_id}")

        series = all_series[trend.related_series_id]
        xs_base = np.array(series.data[series.x_field])
        xs = xs_base.reshape(-1, 1)
        ys = np.array(series.data[series.y_field]).reshape(-1, 1)
        model = KNeighborsRegressor(min(10, xs_base.size), weights='distance')

        model.fit(xs, ys)
        xs_predict = np.linspace(xs.min(), xs.max(), 100).reshape(-1, 1)
        ys_predict = model.predict(xs_predict)

        trace = pgo.Scatter(
            x=xs_predict.reshape(1, -1)[0],
            y=ys_predict.reshape(1, -1)[0],
            name=trend_name,
            mode='lines',
            line={
                'dash': 'dash',
                'color': 'red',
            }
        )

        fig.add_trace(trace)

    def __add_ml_linear_trend_trace(self, trend_name: str, trend: PlotlyTrendLine, fig: pgo.Figure):
        all_series = {**self.series, **self.averages}
        if trend.related_series_id not in all_series:
            raise InvalidTrendLine(f"Missing related series: {trend.related_series_id}")

        series = all_series[trend.related_series_id]
        xs = np.array(series.data[series.x_field]).reshape(-1, 1)
        ys = np.array(series.data[series.y_field]).reshape(-1, 1)
        model = LinearRegression()

        model.fit(xs, ys)
        xs_predict = np.linspace(xs.min(), xs.max(), 100).reshape(-1, 1)
        ys_predict = model.predict(xs_predict)

        trace = pgo.Scatter(
            x=xs_predict.reshape(1, -1)[0],
            y=ys_predict.reshape(1, -1)[0],
            name=trend_name,
            mode='lines',
            line={
                'dash': 'dash',
                'color': 'red',
            }
        )

        fig.add_trace(trace)

    def __add_const_trend_trace(self, trend_name: str, trend: PlotlyTrendLine, fig: pgo.Figure):
        all_series = {**self.series, **self.averages}
        if trend.related_series_id not in all_series:
            raise InvalidTrendLine(f"Missing related series: {trend.related_series_id}")
        if trend.constant is None:
            raise InvalidTrendLine(f"Constant must be specified to draw const trendline")

        series = all_series[trend.related_series_id]
        xs_range = series.data[series.x_field]
        xs = np.linspace(xs_range.min(), xs_range.max(), 10)
        ys = np.full(10, trend.constant)

        trace = pgo.Scatter(
            x=xs,
            y=ys,
            name=trend_name,
            mode='lines',
            line={
                'dash': 'dash',
                'color': 'red',
                'width': 0.4,
            }
        )

        fig.add_trace(trace)


class PlotlyExporter:
    def __init__(self, plot: pgo.Figure):
        self.plot = plot

    def write_image(self,
                    file=None,
                    width=1920,
                    height=1080,
                    scale=None,  # if None use default
                    image_format="png"):
        if file is None:
            file = f"{uuid4()}_plotly"

        self.plot.write_image(
            file=file,
            format=image_format,
            width=width,
            height=height,
            scale=scale,
            engine="kaleido"
        )
