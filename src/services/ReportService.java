package services;

import models.Project;
import models.StatusReport;
import models.TaskStatus;

import java.util.Locale;

// Builds and prints project completion reports.
public class ReportService {

    private final TaskService taskService;

    public ReportService(TaskService taskService) {
        this.taskService = taskService;
    }

    // Builds a status snapshot for one project.
    private StatusReport makeReport(Project project) {
        return new StatusReport(
                project.getId(),
                project.getName(),
                taskService.countByStatus(project, TaskStatus.COMPLETED),
                taskService.countByStatus(project, TaskStatus.PENDING),
                taskService.countByStatus(project, TaskStatus.IN_PROGRESS),
                project.getTaskCount(),
                taskService.calculateCompletionPercentage(project),
                project.isCompleted());
    }

    // Prints a completion summary table for all projects.
    public void printReport(ProjectService projectService) {
        int count = projectService.getCount();
        if (count == 0) {
            System.out.println("No projects found.");
            return;
        }

        double[] percentages = new double[count];

        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-10s | %-20s | %5s | %9s | %s%n",
                "PROJECT ID", "PROJECT NAME", "TASKS", "COMPLETED", "PROGRESS (%)");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");

        for (int i = 0; i < count; i++) {
            StatusReport report = makeReport(projectService.getAt(i));
            System.out.printf(Locale.US, "%-10s | %-20s | %5d | %9d | %8.2f%%%n",
                    report.getProjectId(), report.getProjectName(), report.getTotalTasks(),
                    report.getCompletedTasks(), report.getCompletionPercent());
            percentages[i] = report.getCompletionPercent();
        }

        double average = averageOf(percentages);

        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf(Locale.US, "AVERAGE COMPLETION: %.2f%%%n", average);
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
    }

    // Prints a detailed status report for one project.
    public void printReport(Project project) {
        StatusReport report = makeReport(project);
        System.out.println("Project ID: " + report.getProjectId());
        System.out.println("Name: " + report.getProjectName());
        System.out.println("Total tasks: " + report.getTotalTasks());
        System.out.println("Completed tasks: " + report.getCompletedTasks());
        System.out.println("Pending tasks: " + report.getPendingTasks());
        System.out.println("In progress tasks: " + report.getInProgressTasks());
        System.out.printf(Locale.US, "Completion %%: %.2f%%%n", report.getCompletionPercent());
        if (report.getTotalTasks() == 0) {
            System.out.println("This project has no tasks.");
        } else if (report.isComplete()) {
            System.out.println("This project is complete.");
        }
    }

    // Returns the average completion percentage across all projects.
    public double calculateAverageCompletion(ProjectService projectService) {
        int count = projectService.getCount();
        if (count == 0) {
            return 0.0;
        }
        double[] percentages = new double[count];
        for (int i = 0; i < count; i++) {
            percentages[i] = taskService.calculateCompletionPercentage(projectService.getAt(i));
        }
        return averageOf(percentages);
    }

    // Calculates the average of the given percentage values.
    private double averageOf(double[] percentages) {
        double sum = 0;
        for (int i = 0; i < percentages.length; i++) {
            sum += percentages[i];
        }
        return sum / percentages.length;
    }
}
