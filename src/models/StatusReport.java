package models;

import java.util.Locale;

public class StatusReport {

    private final String projectId;
    private final String projectName;
    private final int completedTasks;
    private final int pendingTasks;
    private final int inProgressTasks;
    private final int totalTasks;
    private final double completionPercent;
    private final boolean complete;

    public StatusReport(Project project) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.completedTasks = project.countByStatus(TaskStatus.COMPLETED);
        this.pendingTasks = project.countByStatus(TaskStatus.PENDING);
        this.inProgressTasks = project.countByStatus(TaskStatus.IN_PROGRESS);
        this.totalTasks = project.getTaskCount();
        this.completionPercent = project.getCompletionPercentage();
        this.complete = project.isCompleted();
    }

    public void printRow() {
        System.out.printf(Locale.US, "%-10s | %-20s | %5d | %9d | %8.2f%%%n",
                projectId, projectName, totalTasks, completedTasks, completionPercent);
    }

    public void print() {
        System.out.println("Project ID: " + projectId);
        System.out.println("Name: " + projectName);
        System.out.println("Total tasks: " + totalTasks);
        System.out.println("Completed tasks: " + completedTasks);
        System.out.println("Pending tasks: " + pendingTasks);
        System.out.println("In progress tasks: " + inProgressTasks);
        System.out.printf(Locale.US, "Completion %%: %.2f%%%n", completionPercent);
        if (totalTasks == 0) {
            System.out.println("This project has no tasks.");
        } else if (complete) {
            System.out.println("This project is complete.");
        }
    }

    public double getCompletionPercent() {
        return completionPercent;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public int getInProgressTasks() {
        return inProgressTasks;
    }
}
