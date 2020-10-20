class PlotsException(Exception):
    """Base plots exception"""
    pass


class DuplicatedSeriesName(PlotsException):
    pass


class InvalidPlotsConfiguration(PlotsException):
    pass
