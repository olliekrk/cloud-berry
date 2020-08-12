from typing import List

import matplotlib.pyplot as plt

from .wrappers import DataSeries


class DataSeriesPlots:
    def __init__(self, series: DataSeries) -> None:
        self.series = series

    def show(self,
             x_field: str,
             y_field: str,
             figsize=(15, 5),
             kind='scatter',
             color='red',
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
                figsize=(15, 5),
                kind='scatter',
                color='red',
                title=None) -> plt.Axes:
        pass
        # merged_df = DataHelpers.merge_data_frames(data_frames)
        # fig, ax = plt.subplots(figsize=figsize)
        # for key, grp in merged_df.groupby([groupby]):
        #     ax.plot(grp[x_field], grp[y_field], label=key)
        # ax.legend()
        # return ax
