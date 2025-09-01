package org.pahappa.systems.kpiTracker.core.services;

import org.pahappa.systems.kpiTracker.models.goalMgt.OrganisationGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.DepartmentGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.TeamGoal;
import org.pahappa.systems.kpiTracker.models.goalMgt.IndividualGoal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to verify the MBO system integration is working correctly
 */
@Service
public class MboSystemVerificationService {

    @Autowired
    private OrganisationGoalService organisationGoalService;

    @Autowired
    private DepartmentGoalService departmentGoalService;

    @Autowired
    private TeamGoalService teamGoalService;

    @Autowired
    private IndividualGoalService individualGoalService;

    @Autowired
    private ProgressCalculationService progressCalculationService;

    /**
     * Verify all goal services are properly wired
     */
    public boolean verifyServiceIntegration() {
        try {
            // Test basic service operations with individual error handling
            boolean orgServiceOk = false;
            boolean deptServiceOk = false;
            boolean teamServiceOk = false;
            boolean individualServiceOk = false;
            boolean progressServiceOk = false;

            try {
                List<OrganisationGoal> orgGoals = organisationGoalService.findAllActive();
                orgServiceOk = true;
            } catch (Exception e) {
                System.err.println("OrganisationGoalService test failed: " + e.getMessage());
            }

            try {
                List<DepartmentGoal> deptGoals = departmentGoalService.findAllActive();
                deptServiceOk = true;
            } catch (Exception e) {
                System.err.println("DepartmentGoalService test failed: " + e.getMessage());
            }

            try {
                List<TeamGoal> teamGoals = teamGoalService.findAllActive();
                teamServiceOk = true;
            } catch (Exception e) {
                System.err.println("TeamGoalService test failed: " + e.getMessage());
            }

            try {
                List<IndividualGoal> individualGoals = individualGoalService.findAllActive();
                individualServiceOk = true;
            } catch (Exception e) {
                System.err.println("IndividualGoalService test failed: " + e.getMessage());
            }

            try {
                double overallProgress = progressCalculationService.calculateOverallOrganisationalProgress();
                progressServiceOk = true;
            } catch (Exception e) {
                System.err.println("ProgressCalculationService test failed: " + e.getMessage());
            }

            // Return true if at least the core services are working
            return orgServiceOk && deptServiceOk && teamServiceOk && individualServiceOk;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get system status summary
     */
    public String getSystemStatus() {
        try {
            // Use safer methods that might not throw exceptions
            long orgGoalCount = 0;
            long deptGoalCount = 0;
            long teamGoalCount = 0;
            long individualGoalCount = 0;

            try {
                orgGoalCount = organisationGoalService.countActiveGoals();
            } catch (Exception e) {
                // Log but continue with other counts
                System.err.println("Error counting organisation goals: " + e.getMessage());
            }

            try {
                deptGoalCount = departmentGoalService.countActiveGoals();
            } catch (Exception e) {
                System.err.println("Error counting department goals: " + e.getMessage());
            }

            try {
                teamGoalCount = teamGoalService.countActiveGoals();
            } catch (Exception e) {
                System.err.println("Error counting team goals: " + e.getMessage());
            }

            try {
                individualGoalCount = individualGoalService.countActiveGoals();
            } catch (Exception e) {
                System.err.println("Error counting individual goals: " + e.getMessage());
            }

            double overallProgress = 0.0;
            try {
                overallProgress = progressCalculationService.calculateOverallOrganisationalProgress();
            } catch (Exception e) {
                System.err.println("Error calculating overall progress: " + e.getMessage());
            }

            return String.format(
                    "MBO System Status: Organisation Goals: %d, Department Goals: %d, Team Goals: %d, Individual Goals: %d, Overall Progress: %.2f%%",
                    orgGoalCount, deptGoalCount, teamGoalCount, individualGoalCount, overallProgress);
        } catch (Exception e) {
            return "MBO System Status: Error - " + e.getMessage();
        }
    }
}
