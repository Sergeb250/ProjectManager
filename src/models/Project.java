package models;

import interfaces.Completable;

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

    // True only when every stored task is completed. A project with no tasks is not completed.
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

    // The task array stays private. TaskService uses this to read one slot.
    public Task readTask(int index) {
        return tasks[index];
    }

    // TaskService uses this to store a task, or to clear a slot with null when a task is removed.
    public void writeTask(int index, Task task) {
        tasks[index] = task;
    }

    // TaskService updates the count after it adds or removes a task.
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
