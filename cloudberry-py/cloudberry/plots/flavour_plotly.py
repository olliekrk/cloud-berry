from typing import Dict

import plotly.graph_objects as pgo

from .properties import PlotProperties, PlotSeriesKind
from .series import PlotSeries
from .utils import PlotUtils


class PlotlyFlavourPlot:
    def __init__(self,
                 series: Dict[str, PlotSeries],
                 trend_series: Dict[str, PlotSeries],
                 avg_series: Dict[str, PlotSeries],
                 properties: PlotProperties):
        self.series = series
        self.trends = trend_series
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
