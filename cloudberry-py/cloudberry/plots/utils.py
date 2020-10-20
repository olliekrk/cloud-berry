from typing import Tuple

from colorhash import ColorHash


class PlotUtils:
    @staticmethod
    def series_color_rgb(series_name: str) -> Tuple[int, int, int]:
        return ColorHash(series_name).rgb

    @staticmethod
    def series_color_hex(series_name: str) -> Tuple[int, int, int]:
        return ColorHash(series_name).hex
