package services;

import models.Project;
import models.Task;
import models.TaskStatus;
import utils.exceptions.TaskNotFoundException;

// Handles task operations across projects.
public class TaskService {

    private final ProjectService projectService;

    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }

    // Returns completed tasks as a percentage of all tasks in the project.
    public double calculateCompletionPercentage(Project project) {
        if (project.getTaskCount() == 0) {
            return 0.0;
        }
        return (countByStatus(project, TaskStatus.COMPLETED) * 100.0) / project.getTaskCount();
    }

    // Counts how many tasks in the project have the given status.
    public int countByStatus(Project project, TaskStatus status) {
        int count = 0;
        for (int i = 0; i < project.getTaskCount(); i++) {
            if (project.readTask(i).getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    // Returns how many tasks are completed.
    public int getCompletedCount(Project project) {
        return countByStatus(project, TaskStatus.COMPLETED);
    }

    // True when the project cannot accept more tasks.
    public boolean isTaskListFull(Project project) {
        return project.getTaskCount() >= Project.MAX_TASKS;
    }

    // Adds an existing task object to the project; returns false if full.
    public boolean addTask(Project project, Task task) {
        if (isTaskListFull(project)) {
            return false;
        }
        project.writeTask(project.getTaskCount(), task);
        project.setTaskCount(project.getTaskCount() + 1);
        return true;
    }

    // True when a task with the same name already exists in the project.
    public boolean hasTaskName(Project project, String name) {
        for (int i = 0; i < project.getTaskCount(); i++) {
            if (project.readTask(i).getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // Finds a task by ID inside one project, or returns null.
    public Task findTaskById(Project project, String taskId) {
        for (int i = 0; i < project.getTaskCount(); i++) {
            if (project.readTask(i).getId().equalsIgnoreCase(taskId)) {
                return project.readTask(i);
            }
        }
        return null;
    }

    // Returns the task at the given index, or throws if out of range.
    public Task getTaskAt(Project project, int index) {
        if (index < 0 || index >= project.getTaskCount()) {
            throw new IllegalArgumentException("Task index is out of range.");
        }
        return project.readTask(index);
    }

    // Removes a task by ID from one project and shifts the remaining tasks left.
    public Task removeTaskById(Project project, String taskId) {
        for (int i = 0; i < project.getTaskCount(); i++) {
            if (project.readTask(i).getId().equalsIgnoreCase(taskId)) {
                Task removed = project.readTask(i);
                for (int j = i; j < project.getTaskCount() - 1; j++) {
                    project.writeTask(j, project.readTask(j + 1));
                }
                project.writeTask(project.getTaskCount() - 1, null);
                project.setTaskCount(project.getTaskCount() - 1);
                return removed;
            }
        }
        return null;
    }

    // Searches every project for a task with the given ID.
    public Task findById(String taskId) {
        for (int i = 0; i < projectService.getCount(); i++) {
            Task task = findTaskById(projectService.getAt(i), taskId);
            if (task != null) {
                return task;
            }
        }
        return null;
    }

    // Finds a task by ID or throws TaskNotFoundException.
    public Task findRequired(String taskId) throws TaskNotFoundException {
        Task task = findById(taskId);
        if (task == null) {
            throw new TaskNotFoundException("Task '" + taskId + "' was not found in the project.");
        }
        return task;
    }

    // Creates and adds a new task; returns null if the list is full or the name exists.
    public Task addTask(Project project, String name, String assignedTo, TaskStatus status, int hours) {
        if (isTaskListFull(project) || hasTaskName(project, name)) {
            return null;
        }
        Task task = new Task(name, assignedTo, status, hours);
        if (!addTask(project, task)) {
            return null;
        }
        return task;
    }

    // Changes the status of an existing task.
    public void updateStatus(Task task, TaskStatus status) {
        task.setStatus(status);
    }

    // Removes a task by ID from any project.
    public Task remove(String taskId) {
        for (int i = 0; i < projectService.getCount(); i++) {
            Task removed = removeTaskById(projectService.getAt(i), taskId);
            if (removed != null) {
                return removed;
            }
        }
        return null;
    }

    // Removes a task by ID from the given project.
    public Task remove(Project project, String taskId) {
        return removeTaskById(project, taskId);
    }
}
