package com.cloudberry.cloudberry.topology.model.filtering;

import com.cloudberry.cloudberry.kafka.event.generic.ComputationEvent;
import com.cloudberry.cloudberry.topology.model.operators.EqualityOperator;
import com.cloudberry.cloudberry.topology.model.operators.LogicalOperator;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public abstract class ExpressionChecker {

    public static boolean check(ComputationEvent event, FilterExpression exp) {
        if (exp.getOperator() == LogicalOperator.OR) {
            // using suppliers for laziness
            Supplier<Boolean> predicatesMet = () -> exp.getPredicates().stream().anyMatch(p -> check(event, p));
            Supplier<Boolean> expressionsMet = () -> exp.getExpressions().stream().anyMatch(e -> check(event, e));
            return predicatesMet.get() || expressionsMet.get();
        } else if (exp.getOperator() == LogicalOperator.AND) {
            var predicatesMet = exp.getPredicates().stream().allMatch(p -> check(event, p));
            var expressionsMet = exp.getExpressions().stream().allMatch(e -> check(event, e));
            return predicatesMet && expressionsMet;
        } else {
            return false;
        }
    }

    public static boolean check(ComputationEvent event, FilterPredicate exp) {
        var name = exp.getName();
        var value = exp.getValue();
        var eventValue = exp.isField() ? event.getFields().get(name) : event.getTags().get(name);
        return switch (exp.getType()) {
            case RAW -> compareAsStrings(exp.getOperator(), value, eventValue);
            case BOOLEAN -> compareAsBooleans(exp.getOperator(), value, eventValue);
            case NUMERIC -> compareAsDoubles(exp.getOperator(), value, eventValue);
        };
    }

    private static boolean compareAsStrings(EqualityOperator operator, String value, Object eventValue) {
        String parsedEventValue = null;
        try {
            if (eventValue instanceof Number) {
                parsedEventValue = String.valueOf(((Number) eventValue).doubleValue());
            } else if (eventValue instanceof String) {
                parsedEventValue = (String) eventValue;
            }

            return switch (operator) {
                case EQ -> value.equals(parsedEventValue);
                case NEQ -> !value.equals(parsedEventValue);
                default -> false;
            };
        } catch (ClassCastException | NumberFormatException e) {
            log.info("String value could not be parsed properly.");
            return false;
        }
    }

    private static boolean compareAsBooleans(EqualityOperator operator, String value, Object eventValue) {
        Boolean parsedValue = Boolean.valueOf(value);
        Boolean parsedEventValue = null;
        try {
            if (eventValue instanceof Boolean) {
                parsedEventValue = (Boolean) eventValue;
            } else if (eventValue instanceof String) {
                parsedEventValue = Boolean.valueOf((String) eventValue);
            }

            return switch (operator) {
                case EQ -> parsedValue.equals(parsedEventValue);
                case NEQ -> !parsedValue.equals(parsedEventValue);
                default -> false;
            };
        } catch (ClassCastException e) {
            log.info("Boolean value could not be parsed properly.");
            return false;
        }
    }

    private static boolean compareAsDoubles(EqualityOperator operator, String value, Object eventValue) {
        Double parsedValue = Double.valueOf(value);
        Double parsedEventValue = null;
        try {
            if (eventValue instanceof Number) {
                parsedEventValue = ((Number) eventValue).doubleValue();
            } else if (eventValue instanceof String) {
                parsedEventValue = Double.valueOf((String) eventValue);
            }

            if (parsedEventValue == null) {
                return false;
            }

            return switch (operator) {
                case EQ -> parsedValue.equals(parsedEventValue);
                case NEQ -> !parsedValue.equals(parsedEventValue);
                case LT -> parsedEventValue < parsedValue;
                case LTE -> parsedEventValue <= parsedValue;
                case GT -> parsedEventValue > parsedValue;
                case GTE -> parsedEventValue >= parsedValue;
            };
        } catch (ClassCastException | NumberFormatException e) {
            log.info("Numeric value could not be parsed properly.");
            return false;
        }
    }


}
