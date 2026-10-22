package services;

import interfaces.TaskFilter;
import models.Project;
import models.Task;
import models.TaskStatus;
import utils.exceptions.TaskNotFoundException;

// Handles task operations. Tasks are stored in each project's ArrayList.
public class TaskService {

    private final ProjectService projectService;

    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public double calculateCompletionPercentage(Project project) {
        return project.getCompletionRate();
    }

    public int countByStatus(Project project, TaskStatus status) {
        TaskFilter filter = task -> task.getStatus() == status;
        return (int) project.getTasks().stream().filter(filter::test).count();
    }

    public int getCompletedCount(Project project) {
        return countByStatus(project, TaskStatus.COMPLETED);
    }

    // Lists are not fixed-size anymore.
    public boolean isTaskListFull(Project project) {
        return false;
    }

    public boolean addTask(Project project, Task task) {
        project.getTasks().add(task);
        return true;
    }

    public boolean hasTaskName(Project project, String name) {
        return project.getTasks().stream()
                .anyMatch(task -> task.getName().equalsIgnoreCase(name));
    }

    public Task findTaskById(Project project, String taskId) {
        return project.getTasks().stream()
                .filter(task -> task.getId().equalsIgnoreCase(taskId))
                .findFirst()
                .orElse(null);
    }

    public Task getTaskAt(Project project, int index) {
        if (index < 0 || index >= project.getTaskCount()) {
            throw new IllegalArgumentException("Task index is out of range.");
        }
        return project.getTasks().get(index);
    }

    public Task removeTaskById(Project project, String taskId) {
        Task removed = findTaskById(project, taskId);
        if (removed == null) {
            return null;
        }
        project.getTasks().remove(removed);
        return removed;
    }

    public Task findById(String taskId) {
        for (int i = 0; i < projectService.getCount(); i++) {
            Task task = findTaskById(projectService.getAt(i), taskId);
            if (task != null) {
                return task;
            }
        }
        return null;
    }

    public Task findRequired(String taskId) throws TaskNotFoundException {
        Task task = findById(taskId);
        if (task == null) {
            throw new TaskNotFoundException("Task '" + taskId + "' was not found in the project.");
        }
        return task;
    }

    public Task addTask(Project project, String name, String assignedTo, TaskStatus status, int hours) {
        if (hasTaskName(project, name)) {
            return null;
        }
        Task task = new Task(name, assignedTo, status, hours);
        addTask(project, task);
        return task;
    }

    public void updateStatus(Task task, TaskStatus status) {
        task.setStatus(status);
    }

    public Task remove(String taskId) {
        for (int i = 0; i < projectService.getCount(); i++) {
            Task removed = removeTaskById(projectService.getAt(i), taskId);
            if (removed != null) {
                return removed;
            }
        }
        return null;
    }

    public Task remove(Project project, String taskId) {
        return removeTaskById(project, taskId);
    }
}
