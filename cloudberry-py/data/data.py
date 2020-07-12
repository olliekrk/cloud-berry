import pandas as pd
import matplotlib.pyplot as plt


class DataSeries:
    def __init__(self, series_name: str, data: list) -> None:
        self.series_name = series_name
        self.data = data

    def get_data_frame(self) -> pd.DataFrame:
        df = pd.DataFrame(self.data)
        df['series_name'] = self.series_name
        return df

    def get_plot(self, x, y, kind='scatter', color='red', title=None):
        df = self.get_data_frame()
        axes = df.plot(kind=kind, x=x, y=y, color=color, title=title)
        plt.show()
        return axes
