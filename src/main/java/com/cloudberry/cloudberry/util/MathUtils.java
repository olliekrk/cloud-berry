package com.cloudberry.cloudberry.util;

import java.util.Collection;

public class MathUtils {
    public static Double standardDeviation(Collection<Double> doubles) {
        var length = doubles.size();
        var mean = doubles.stream().reduce(.0, Double::sum) / length;
        var squaresSum = doubles.stream().map(d -> Math.pow(d - mean, 2)).reduce(.0, Double::sum);
        return Math.sqrt(squaresSum / length);
    }
}
