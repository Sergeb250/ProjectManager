package services;

import models.Project;
import models.StatusReport;
import models.TaskStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

// Builds and prints project completion reports.
public class ReportService {

    private final TaskService taskService;

    public ReportService(TaskService taskService) {
        this.taskService = taskService;
    }

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

    public void printReport(ProjectService projectService) {
        List<Project> projects = projectService.getProjects().stream()
                .sorted(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-10s | %-20s | %5s | %9s | %s%n",
                "PROJECT ID", "PROJECT NAME", "TASKS", "COMPLETED", "PROGRESS (%)");
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");

        for (Project project : projects) {
            StatusReport report = makeReport(project);
            System.out.printf(Locale.US, "%-10s | %-20s | %5d | %9d | %8.2f%%%n",
                    report.getProjectId(), report.getProjectName(), report.getTotalTasks(),
                    report.getCompletedTasks(), report.getCompletionPercent());
        }

        double average = projects.stream()
                .mapToDouble(taskService::calculateCompletionPercentage)
                .average()
                .orElse(0.0);

        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf(Locale.US, "AVERAGE COMPLETION: %.2f%%%n", average);
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
    }

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

    public double calculateAverageCompletion(ProjectService projectService) {
        return projectService.getProjects().stream()
                .mapToDouble(taskService::calculateCompletionPercentage)
                .average()
                .orElse(0.0);
    }
}
