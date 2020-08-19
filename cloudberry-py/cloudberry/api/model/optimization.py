from enum import Enum


class OptimizationKind(Enum):
    FINAL_VALUE = 'FINAL_VALUE',
    AREA_UNDER_CURVE = 'AREA_UNDER_CURVE'


class OptimizationGoal(Enum):
    MAX = 'MAX',
    MIN = 'MIN'
