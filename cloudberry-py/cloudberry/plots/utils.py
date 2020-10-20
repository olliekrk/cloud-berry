from colorhash import ColorHash


class PlotUtils:
    @staticmethod
    def series_color(series_name: str):
        color_hash = ColorHash(series_name)
        return tuple(map(lambda c: c / 255.0, color_hash.rgb))
