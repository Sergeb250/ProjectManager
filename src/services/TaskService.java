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

    public Task addTask(Project project, String name, String assignedTo, TaskStatus status, int hours) {
        if (project.isTaskListFull() || project.hasTaskName(name)) {
            return null;
        }
        Task task = new Task(name, assignedTo, status, hours);
        if (!project.addTask(task)) {
            return null;
        }
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

    public Task remove(Project project, String taskId) {
        return project.removeTaskById(taskId);
    }
}
