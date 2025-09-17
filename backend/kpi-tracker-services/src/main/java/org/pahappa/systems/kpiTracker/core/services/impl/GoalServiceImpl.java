package org.pahappa.systems.kpiTracker.core.services.impl;

import com.googlecode.genericdao.search.Search;
import org.pahappa.systems.kpiTracker.constants.GoalLevel;
import org.pahappa.systems.kpiTracker.core.dao.GoalDao;
import org.pahappa.systems.kpiTracker.core.services.GoalService;
import org.pahappa.systems.kpiTracker.models.goalCreation.Goal;
import org.pahappa.systems.kpiTracker.models.staff.Staff;
import org.sers.webutils.model.RecordStatus;
import org.sers.webutils.model.exception.OperationFailedException;
import org.sers.webutils.model.exception.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class GoalServiceImpl extends GenericServiceImpl<Goal> implements GoalService {
    @Autowired
    private GoalDao goalDao; // Adjust based on your setup

    // @Override
    // public List<Goal> getPotentialParentGoals(GoalLevel childLevel) {
    // List<Goal> potentialParents = new ArrayList<>();
    // if (childLevel == null) {
    // return potentialParents; // Return empty list if no child level
    // }
    //
    // // Fetch goals with levels that can be parents of the childLevel
    // GoalLevel parentLevel = GoalLevel.getParentLevel(childLevel);
    // while (parentLevel != null) {
    // potentialParents.addAll(getGoalsByLevel(parentLevel));
    // parentLevel = GoalLevel.getParentLevel(parentLevel); // Move up the hierarchy
    // }
    // return potentialParents;
    // }

    @Override
    public List<Goal> getGoalsByLevel(GoalLevel goalLevel) {
        // Query database for goals with the specified level using JOIN FETCH to avoid
        // LazyInitializationException
        return goalDao.findByLevelWithOwner(goalLevel, RecordStatus.ACTIVE);
    }

    @Override
    public List<Goal> getGoalsByOwner(Staff owner, GoalLevel goalLevel) {
        // Query database for goals by owner with JOIN FETCH to avoid
        // LazyInitializationException
        return goalDao.findByOwnerWithOwner(owner, goalLevel, RecordStatus.ACTIVE);
    }

    @Override
    public boolean isDeletable(Goal instance) throws OperationFailedException {
        return true;
    }

    public void updateGoalProgress(Goal goal) {
        if (goal == null) {
            return;
        }

        // --- This is where your core business logic goes ---
        // 1. Find all KPIs and/or child Goals for the given 'goal'.
        // 2. Calculate the new progress based on their contributions and weights.
        // For example: double calculatedProgress = calculateProgressFromChildren(goal);
        //
        // 3. Set the new progress and status on the goal.
        // goal.setProgress(calculatedProgress);
        // if (calculatedProgress >= 100) {
        // goal.setGoalStatus(GoalStatus.COMPLETED);
        // } else {
        // // Logic for Active/Overdue status
        // }
        //
        // 4. Save the updated goal to the database.
        // super.save(goal); // or whatever your save method is
        //
        // 5. IMPORTANT: Recursively update the parent goal.
        // if (goal.getParentGoal() != null) {
        // updateGoalProgress(goal.getParentGoal());
        // }
    }

    @Override
    public Goal saveInstance(Goal entityInstance) throws ValidationFailedException, OperationFailedException {
        // Validate name
        if (entityInstance.getGoalName() == null || entityInstance.getGoalName().trim().isEmpty()) {
            throw new ValidationFailedException("Goal name is required");
        }

        // Validate description
        if (entityInstance.getDescription() == null || entityInstance.getDescription().trim().isEmpty()) {
            throw new ValidationFailedException("Goal description is required");
        }

        // Validate weight range
        if (entityInstance.getWeight() < 0 || entityInstance.getWeight() > 100) {
            throw new ValidationFailedException("Weight must be between 0 and 100");
        }

        // Validate department for non-ORGANISATION goals
        if (entityInstance.getGoalLevel() != GoalLevel.ORGANISATION && entityInstance.getDepartment() == null) {
            throw new ValidationFailedException(
                    "Department is required for " + entityInstance.getGoalLevel().getDisplayName());
        }

        // Validate parent for non-ORGANISATION goals
        if (entityInstance.getGoalLevel() != GoalLevel.ORGANISATION && entityInstance.getParentGoal() == null) {
            throw new ValidationFailedException(
                    "Parent goal is required for " + entityInstance.getGoalLevel().getDisplayName());
        }

        // For TEAM or INDIVIDUAL goals, ensure department matches parent goal's
        // department
        if (entityInstance.getGoalLevel() == GoalLevel.TEAM || entityInstance.getGoalLevel() == GoalLevel.INDIVIDUAL) {
            if (entityInstance.getParentGoal() != null && entityInstance.getDepartment() != null) {
                if (!entityInstance.getDepartment().getId()
                        .equals(entityInstance.getParentGoal().getDepartment().getId())) {
                    throw new ValidationFailedException(
                            entityInstance.getGoalLevel().getDisplayName() + " department must match parent "
                                    + GoalLevel.getParentLevel(entityInstance.getGoalLevel()).getDisplayName()
                                    + "'s department");
                }
            }
        }

        // Validate weight sum for child goals
        if (entityInstance.getParentGoal() != null) {
            List<Goal> siblings = getChildGoals(entityInstance.getParentGoal());
            List<Goal> filteredSiblings = new ArrayList<>();
            // Exclude current goal if editing
            if (entityInstance.getId() != null) {
                for (Goal sibling : siblings) {
                    if (!sibling.getId().equals(entityInstance.getId())) {
                        filteredSiblings.add(sibling);
                    }
                }
            } else {
                filteredSiblings = siblings;
            }
            double sumWeights = 0.0;
            for (Goal sibling : filteredSiblings) {
                sumWeights += sibling.getWeight();
            }
            sumWeights += entityInstance.getWeight();
            if (sumWeights > 100) {
                throw new ValidationFailedException(
                        "Total weight for child goals of this parent exceeds 100% (current sum: " + sumWeights + "%)");
            }
        }

        return goalDao.save(entityInstance);
    }

    @Override
    public List<Goal> getPotentialParentGoals(GoalLevel childLevel) {
        if (childLevel == null) {
            return new ArrayList<>();
        }
        GoalLevel parentLevel = GoalLevel.getParentLevel(childLevel);
        if (parentLevel == null) {
            return new ArrayList<>();
        }
        // Use eager loading to avoid LazyInitializationException in dropdown
        return goalDao.findByLevelWithOwner(parentLevel, RecordStatus.ACTIVE);
    }

    @Override
    public List<Goal> getChildGoals(Goal parentGoal) {
        if (parentGoal == null) {
            return new ArrayList<>();
        }
        // Use eager loading to avoid LazyInitializationException
        Search search = new Search(Goal.class);
        search.addFilterEqual("parentGoal", parentGoal);
        search.addFilterEqual("recordStatus", RecordStatus.ACTIVE);
        return goalDao.findBySearchWithOwner(search, 0, Integer.MAX_VALUE);
    }
}