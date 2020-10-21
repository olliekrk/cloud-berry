class PlotsException(Exception):
    """Base plots exception"""
    pass


class DuplicatedSeriesName(PlotsException):
    pass


class DuplicatedTrendLine(PlotsException):
    pass


class InvalidPlotsConfiguration(PlotsException):
    pass


class InvalidTrendLine(PlotsException):
    pass
