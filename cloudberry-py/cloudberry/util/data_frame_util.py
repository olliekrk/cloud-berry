from typing import List

import pandas as pd


class DataFrameUtil:
    @staticmethod
    def merge(data_frames: List[pd.DataFrame]) -> pd.DataFrame:
        return pd.concat(data_frames)
