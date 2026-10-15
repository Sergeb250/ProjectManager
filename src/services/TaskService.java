package services;

import models.Project;
import models.Task;
import models.TaskStatus;

public class TaskService {

    private final ProjectService projectService;

    public TaskService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public double calculateCompletionPercentage(Project project) {
        if (project.getTaskCount() == 0) {
            return 0.0;
        }
        return (countByStatus(project, TaskStatus.COMPLETED) * 100.0) / project.getTaskCount();
    }

    public int countByStatus(Project project, TaskStatus status) {
        int count = 0;
        for (int i = 0; i < project.getTaskCount(); i++) {
            if (project.readTask(i).getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public int getCompletedCount(Project project) {
        return countByStatus(project, TaskStatus.COMPLETED);
    }

    public boolean isTaskListFull(Project project) {
        return project.getTaskCount() >= Project.MAX_TASKS;
    }

    public boolean addTask(Project project, Task task) {
        if (isTaskListFull(project)) {
            return false;
        }
        project.writeTask(project.getTaskCount(), task);
        project.setTaskCount(project.getTaskCount() + 1);
        return true;
    }

    public boolean hasTaskName(Project project, String name) {
        for (int i = 0; i < project.getTaskCount(); i++) {
            if (project.readTask(i).getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Task findTaskById(Project project, String taskId) {
        for (int i = 0; i < project.getTaskCount(); i++) {
            if (project.readTask(i).getId().equalsIgnoreCase(taskId)) {
                return project.readTask(i);
            }
        }
        return null;
    }

    public Task getTaskAt(Project project, int index) {
        if (index < 0 || index >= project.getTaskCount()) {
            throw new IllegalArgumentException("Task index is out of range.");
        }
        return project.readTask(index);
    }

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

    public Task findById(String taskId) {
        for (int i = 0; i < projectService.getCount(); i++) {
            Task task = findTaskById(projectService.getAt(i), taskId);
            if (task != null) {
                return task;
            }
        }
        return null;
    }

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
