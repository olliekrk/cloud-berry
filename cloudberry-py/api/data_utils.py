import pandas as pd


def as_data_frame(list_of_dicts: list) -> pd.DataFrame:
    return pd.DataFrame(list_of_dicts)
