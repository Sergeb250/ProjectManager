package models;

public class StatusReport {

    private final String projectId;
    private final String projectName;
    private final int completedTasks;
    private final int pendingTasks;
    private final int inProgressTasks;
    private final int totalTasks;
    private final double completionPercent;
    private final boolean complete;

    public StatusReport(String projectId, String projectName, int completedTasks, int pendingTasks,
                        int inProgressTasks, int totalTasks, double completionPercent, boolean complete) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.inProgressTasks = inProgressTasks;
        this.totalTasks = totalTasks;
        this.completionPercent = completionPercent;
        this.complete = complete;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
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

    public boolean isComplete() {
        return complete;
    }
}
