import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import org.junit.jupiter.api.Test;
import services.ProjectService;
import services.StreamService;
import services.TaskService;
import utils.RegexValidator;
import utils.exceptions.InvalidInputException;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.ValidationUtils.readPrefixedId;

public class StreamOperationsTest {

    @Test
    void testFilterCompletedTasksUsingStreams() {
        SoftwareProject project = new SoftwareProject("Alpha", "Demo", 3, 10, 1000, "Tuyizere", "Java");
        TaskService taskService = new TaskService(new ProjectService());
        taskService.addTask(project, "Design UI", "", TaskStatus.COMPLETED, 2);
        taskService.addTask(project, "Write Tests", "", TaskStatus.PENDING, 3);

        List<Task> completed = StreamService.completedTasks(project.getTasks());
        List<String> names = StreamService.taskNames(project.getTasks());

        assertEquals(1, completed.size());
        assertEquals("Design UI", names.get(0));
        assertEquals("Write Tests", names.get(1));
        assertEquals(50.0, StreamService.totalCompletion(List.of(project)), 0.001);
    }

    @Test
    void regexAcceptsProjectAndTaskIds() {
        assertTrue(RegexValidator.isProjectId("PRJ001"));
        assertTrue(RegexValidator.isTaskId("TSK004"));
        assertFalse(RegexValidator.isProjectId("TX01"));
        assertFalse(RegexValidator.isTaskId("T001"));
        assertTrue(RegexValidator.isEmail("tuyizere@amalitech.com"));
    }

    @Test
    void badTaskIdThrows() {
        Scanner scanner = new Scanner("TX01\n");
        assertThrows(InvalidInputException.class,
                () -> readPrefixedId(scanner, "Enter task ID: ", "TSK", false));
    }
}
