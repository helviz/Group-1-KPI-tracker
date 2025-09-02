package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.BaseGoal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Utility class for progress calculations across the MBO system
 */
@Component
public class ProgressCalculationUtil {

    /**
     * Calculate weighted average progress for a collection of goals
     * 
     * @param goals List of goals to calculate progress for
     * @return Weighted average progress
     */
    public static double calculateWeightedAverage(List<? extends BaseGoal> goals) {
        if (goals == null || goals.isEmpty()) {
            return 0.0;
        }

        BigDecimal totalWeightedProgress = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (BaseGoal goal : goals) {
            if (goal != null && goal.getProgress() != null && goal.getContributionToParent() != null) {
                BigDecimal progress = goal.getProgress();
                BigDecimal contribution = goal.getContributionToParent();

                totalWeightedProgress = totalWeightedProgress.add(progress.multiply(contribution));
                totalWeight = totalWeight.add(contribution);
            }
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        return totalWeightedProgress.divide(totalWeight, 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Calculate simple average progress for a collection of goals
     * 
     * @param goals List of goals to calculate progress for
     * @return Simple average progress
     */
    public static double calculateSimpleAverage(List<? extends BaseGoal> goals) {
        if (goals == null || goals.isEmpty()) {
            return 0.0;
        }

        BigDecimal totalProgress = BigDecimal.ZERO;
        int goalCount = goals.size();

        for (BaseGoal goal : goals) {
            if (goal != null && goal.getProgress() != null) {
                totalProgress = totalProgress.add(goal.getProgress());
            }
        }

        if (goalCount == 0) {
            return 0.0;
        }

        return totalProgress.divide(BigDecimal.valueOf(goalCount), 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Calculate progress percentage based on current and target values
     * 
     * @param currentValue Current value
     * @param targetValue  Target value
     * @return Progress percentage (0-100)
     */
    public static double calculateProgressPercentage(BigDecimal currentValue, BigDecimal targetValue) {
        if (currentValue == null || targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        if (currentValue.compareTo(targetValue) >= 0) {
            return 100.0;
        }

        return currentValue.divide(targetValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Calculate progress percentage based on current and target values with start
     * value
     * 
     * @param currentValue Current value
     * @param startValue   Start value
     * @param targetValue  Target value
     * @return Progress percentage (0-100)
     */
    public static double calculateProgressPercentageWithStart(BigDecimal currentValue, BigDecimal startValue,
            BigDecimal targetValue) {
        if (currentValue == null || startValue == null || targetValue == null) {
            return 0.0;
        }

        if (targetValue.compareTo(startValue) == 0) {
            return 0.0;
        }

        if (currentValue.compareTo(targetValue) >= 0) {
            return 100.0;
        }

        if (currentValue.compareTo(startValue) <= 0) {
            return 0.0;
        }

        BigDecimal progress = currentValue.subtract(startValue)
                .divide(targetValue.subtract(startValue), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return progress.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Validate progress percentage is within valid range
     * 
     * @param progress Progress percentage to validate
     * @return True if progress is valid (0-100)
     */
    public static boolean isValidProgress(double progress) {
        return progress >= 0.0 && progress <= 100.0;
    }

    /**
     * Clamp progress percentage to valid range (0-100)
     * 
     * @param progress Progress percentage to clamp
     * @return Clamped progress percentage
     */
    public static double clampProgress(double progress) {
        if (progress < 0.0)
            return 0.0;
        if (progress > 100.0)
            return 100.0;
        return progress;
    }
}
