package models;

import interfaces.Completable;

import java.util.Locale;

public abstract class Project implements Completable {

    public static final int MAX_TASKS = 20;

    private static int nextNumber = 1;

    private final String id;
    private final String name;
    private final String description;
    private final int teamSize;
    private final int duration;
    private final double budget;
    private final String leadName;

    private final Task[] tasks;
    private int taskCount;

    public Project(String name, String description, int teamSize, int duration, double budget, String leadName) {
        this.id = String.format("PRJ%03d", nextNumber);
        nextNumber++;
        this.name = name;
        this.description = description;
        this.teamSize = teamSize;
        this.duration = duration;
        this.budget = budget;
        this.leadName = leadName;
        this.tasks = new Task[MAX_TASKS];
        this.taskCount = 0;
    }

    public abstract String getType();

    public abstract String getProjectDetails();

    public void displayProject() {
        displayProject(false);
    }

    public void displayProject(boolean includeTasks) {
        System.out.println("Project Name: " + name);
        System.out.println("Type: " + getType());
        System.out.println("Team Size: " + teamSize);
        System.out.println("Budget: " + formatMoney(budget));
        System.out.println("Description: " + description);
        System.out.println(getProjectDetails());
        if (includeTasks) {
            System.out.println();
            System.out.println("Associated Tasks:");
            printTaskTable();
        }
        System.out.printf(Locale.US, "Completion Rate: %.2f%%%n", getCompletionPercentage());
    }

    public void displayCatalogRow() {
        System.out.printf(Locale.US, "%-8s | %-20s | %-10s | %-9d | %s%n",
                id, fit(name, 20), getType(), teamSize, formatMoney(budget));
        System.out.println("         | Description: " + description);
        System.out.println("──────────────────────────────────────────────────────────────────────────────────────────────");
    }

    public void printTaskTable() {
        if (taskCount == 0) {
            System.out.println("No tasks found.");
            return;
        }
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("%-8s | %-20s | %s%n", "ID", "TASK NAME", "STATUS");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        for (int i = 0; i < taskCount; i++) {
            Task task = tasks[i];
            System.out.printf("%-8s | %-20s | %s%n",
                    task.getId(), fit(task.getName(), 20), task.getStatus().getLabel());
        }
        System.out.println("───────────────────────────────────────────────────────────────────────");
    }

    @Override
    public boolean isCompleted() {
        return taskCount > 0 && countByStatus(TaskStatus.COMPLETED) == taskCount;
    }

    public double getCompletionPercentage() {
        if (taskCount == 0) {
            return 0.0;
        }
        return (countByStatus(TaskStatus.COMPLETED) * 100.0) / taskCount;
    }

    public int countByStatus(TaskStatus status) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public int getCompletedCount() {
        return countByStatus(TaskStatus.COMPLETED);
    }

    public boolean isTaskListFull() {
        return taskCount >= MAX_TASKS;
    }

    public void addTask(Task task) {
        tasks[taskCount] = task;
        taskCount++;
    }

    public boolean hasTaskName(String name) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Task findTaskById(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getId().equalsIgnoreCase(taskId)) {
                return tasks[i];
            }
        }
        return null;
    }

    public Task removeTaskById(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getId().equalsIgnoreCase(taskId)) {
                Task removed = tasks[i];
                for (int j = i; j < taskCount - 1; j++) {
                    tasks[j] = tasks[j + 1];
                }
                tasks[taskCount - 1] = null;
                taskCount--;
                return removed;
            }
        }
        return null;
    }

    private String formatMoney(double amount) {
        if (amount == Math.rint(amount)) {
            return String.format(Locale.US, "$%,.0f", amount);
        }
        return String.format(Locale.US, "$%,.2f", amount);
    }

    private String fit(String value, int width) {
        if (value.length() <= width) {
            return value;
        }
        return value.substring(0, width);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public int getDuration() {
        return duration;
    }

    public double getBudget() {
        return budget;
    }

    public String getLeadName() {
        return leadName;
    }

    public int getTaskCount() {
        return taskCount;
    }
}
