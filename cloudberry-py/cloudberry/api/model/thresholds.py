from enum import Enum


class ThresholdsType(Enum):
    ABSOLUTE = 'ABSOLUTE',
    PERCENTS = 'PERCENTS'


class Thresholds:
    def __init__(self, upper: float = None, lower: float = None):
        self.upper = upper
        self.lower = lower
