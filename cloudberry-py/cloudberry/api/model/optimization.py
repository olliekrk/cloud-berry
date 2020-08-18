from enum import Enum


class OptimizationKind(Enum):
    MAX = 'MAX',
    MIN = 'MIN'


class OptimizationGoal(Enum):
    FINAL_VALUE = 'FINAL_VALUE',
    AREA_UNDER_CURVE = 'AREA_UNDER_CURVE'
