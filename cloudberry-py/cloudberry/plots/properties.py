from enum import Enum

from .flavours import PlottingFlavour


class PlotSeriesKind(Enum):
    SCATTER = 1
    SCATTERLINE = 2
    LINE = 3


class PlotLineKind(Enum):
    CONTINUOUS = 1
    DASHED = 2
    DOTTED = 3


class PlotProperties:
    """Mutable properties for configuring up plotting"""

    @staticmethod
    def default():
        """Get default PlotProperties configuration"""
        return PlotProperties()

    def __init__(self,
                 title: str = None,
                 x_axis_name: str = None,
                 y_axis_name: str = None,
                 show_series: bool = True,
                 show_averages: bool = True,
                 show_trends: bool = True,
                 show_title: bool = True,
                 show_legend: bool = True,
                 show_error_bars: bool = True,
                 flavour: PlottingFlavour = PlottingFlavour.Plotly,
                 default_series_kind: PlotSeriesKind = PlotSeriesKind.SCATTER,
                 default_line_kind: PlotLineKind = PlotLineKind.CONTINUOUS
                 ):
        self.title = title
        self.x_axis_name = x_axis_name
        self.y_axis_name = y_axis_name

        self.show_series = show_series
        self.show_averages = show_averages
        self.show_trends = show_trends
        self.show_title = show_title
        self.show_legend = show_legend
        self.show_error_bars = show_error_bars

        self.flavour = flavour
        self.default_series_kind = default_series_kind
        self.default_line_kind = default_line_kind
