package models;

import interfaces.Completable;

// Base class for all projects; holds shared fields and the task list.
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

    // Creates a project and assigns the next PRJ ID.
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

    // Returns the project type label (e.g. Software or Hardware).
    public abstract String getType();

    // Returns type-specific details for display.
    public abstract String getProjectDetails();

    // True only when every stored task is completed; empty projects are not completed.
    @Override
    public boolean isCompleted() {
        if (taskCount == 0) {
            return false;
        }
        for (int i = 0; i < taskCount; i++) {
            if (!tasks[i].isCompleted()) {
                return false;
            }
        }
        return true;
    }

    // Reads one task slot; used by TaskService.
    public Task readTask(int index) {
        return tasks[index];
    }

    // Writes or clears one task slot; used by TaskService.
    public void writeTask(int index, Task task) {
        tasks[index] = task;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
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
