import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.ProjectService;
import services.TaskService;
import utils.exceptions.TaskNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaskTests {

    private Task task;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        SoftwareProject project = new SoftwareProject("Alpha", "Demo", 3, 10, 1000, "Tuyizere", "Java");
        taskService = new TaskService(new ProjectService());
        task = taskService.addTask(project, "Design", "", TaskStatus.PENDING, 2);
    }

    @Test
    void updateStatusChangesPendingToCompleted() {
        taskService.updateStatus(task, TaskStatus.COMPLETED);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void missingIdThrows() {
        assertThrows(TaskNotFoundException.class, () -> taskService.findRequired("TSK999"));
    }
}
