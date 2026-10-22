package interfaces;

import models.Task;

// A small functional interface. A lambda can decide if a task should be kept.
@FunctionalInterface
public interface TaskFilter {
    boolean test(Task task);
}
