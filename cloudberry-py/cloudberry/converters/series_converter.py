from cloudberry.api import DataSeries, DataSeriesPack

from cloudberry.plots import PlotSeries, PlotSeriesPack


class SeriesConverter:

    @staticmethod
    def data_series_to_plot_series(ds: DataSeries,
                                   x_field: str,
                                   y_field: str,
                                   y_err_field: str = None) -> PlotSeries:
        return PlotSeries(
            name=ds.series_name,
            data=ds.as_data_frame,
            x_field=x_field,
            y_field=y_field,
            y_err_field=y_err_field
        )

    @staticmethod
    def data_series_pack_to_plot_series_pack(ds: DataSeriesPack,
                                             x_field: str,
                                             y_field: str,
                                             y_err_field: str = None) -> PlotSeriesPack:
        def create_plot_series(d):
            return SeriesConverter.data_series_to_plot_series(d, x_field, y_field, y_err_field)

        series = [create_plot_series(d) for d in ds.series]
        average = None if ds.average_series is None else create_plot_series(ds.average_series)
        return PlotSeriesPack(series, [average])
