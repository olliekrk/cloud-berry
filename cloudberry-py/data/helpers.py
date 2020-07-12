from data import DataSeries
import pandas as pd
import matplotlib.pyplot as plt


class DataHelpers:
    @staticmethod
    def get_data_series(raw_series: list) -> list:
        data_series = []
        for series in raw_series:
            data_series.append(DataSeries(series['seriesName'], series['data']))

        return data_series

    @staticmethod
    def merge_data_frames(data_frames: list) -> pd.DataFrame:
        return pd.concat(data_frames)

    @staticmethod
    def comparison_plot(data_frames: list,
                        y_field: str,
                        x_field='_time',
                        groupby='series_name',
                        kind='scatter',
                        figsize=(15, 5)):
        merged_df = DataHelpers.merge_data_frames(data_frames)
        fig, ax = plt.subplots(figsize=figsize)
        for key, grp in merged_df.groupby([groupby]):
            ax.plot(grp[x_field], grp[y_field], label=key, kind=kind)
        ax.legend()
