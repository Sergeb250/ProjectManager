package services;

import models.Project;
import models.Task;

import java.util.List;
import java.util.stream.Collectors;

// Short stream helpers: filter, map, and reduce.
public class StreamService {

    private StreamService() {
    }

    public static List<Task> completedTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    public static List<String> taskNames(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getName)
                .collect(Collectors.toList());
    }

    public static double totalCompletion(List<Project> projects) {
        return projects.stream()
                .map(Project::getCompletionRate)
                .reduce(0.0, Double::sum);
    }
}
