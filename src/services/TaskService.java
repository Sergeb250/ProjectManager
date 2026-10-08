package services;

import models.Project;
import models.Task;
import models.TaskStatus;

public class TaskService {

    private final ProjectService projectService;

    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public Task findById(String taskId) {
        for (int i = 0; i < projectService.getCount(); i++) {
            Task task = projectService.getAt(i).findTaskById(taskId);
            if (task != null) {
                return task;
            }
        }
        return null;
    }

    public Task add(Project project, String name, String assignedTo, TaskStatus status, int hours) {
        if (project.hasTaskName(name)) {
            return null;
        }
        Task task = new Task(name, assignedTo, status, hours);
        project.addTask(task);
        return task;
    }

    public void updateStatus(Task task, TaskStatus status) {
        task.setStatus(status);
    }

    public Task remove(String taskId) {
        for (int i = 0; i < projectService.getCount(); i++) {
            Task removed = projectService.getAt(i).removeTaskById(taskId);
            if (removed != null) {
                return removed;
            }
        }
        return null;
    }
}
