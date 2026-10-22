package models;

import interfaces.Completable;

// Represents one task assigned inside a project.
public class Task implements Completable {

    private static int nextNumber = 1;

    private final String id;
    private final String name;
    private final String assignedTo;
    private final int hours;
    private TaskStatus status;

    // Creates a task and assigns the next TSK ID.
    public Task(String name, String assignedTo, TaskStatus status, int hours) {
        this(nextTaskId(), name, assignedTo, status, hours);
    }

    // Used when a task is loaded from the file and already has an ID.
    public Task(String id, String name, String assignedTo, TaskStatus status, int hours) {
        this.id = id;
        rememberNumber(id);
        this.name = name;
        this.assignedTo = assignedTo;
        this.status = status;
        this.hours = hours;
    }

    @Override
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public int getHours() {
        return hours;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    private static String nextTaskId() {
        String id = String.format("TSK%03d", nextNumber);
        nextNumber++;
        return id;
    }

    private static void rememberNumber(String id) {
        String digits = id.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return;
        }
        int number = Integer.parseInt(digits);
        if (number >= nextNumber) {
            nextNumber = number + 1;
        }
    }
}
