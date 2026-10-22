package utils;

import models.HardwareProject;
import models.Project;
import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import services.ProjectService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Saves and loads projects as a simple json-like text file.
public class FileUtils {

    public static final Path DATA_FILE = Path.of("data", "projects_data.json");

    private FileUtils() {
    }

    public static void saveProjects(Path path, ProjectService projectService) throws IOException {
        List<Project> list = projectService.getProjects().stream()
                .sorted(Comparator.comparing(Project::getId))
                .collect(Collectors.toList());

        StringBuilder text = new StringBuilder();
        text.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            Project project = list.get(i);
            String extra = "";
            if (project instanceof SoftwareProject) {
                extra = ((SoftwareProject) project).getTechnology();
            } else if (project instanceof HardwareProject) {
                extra = ((HardwareProject) project).getEquipment();
            }
            text.append("  {\n");
            text.append("    \"projectId\": \"").append(clean(project.getId())).append("\",\n");
            text.append("    \"name\": \"").append(clean(project.getName())).append("\",\n");
            text.append("    \"type\": \"").append(clean(project.getType())).append("\",\n");
            text.append("    \"description\": \"").append(clean(project.getDescription())).append("\",\n");
            text.append("    \"teamSize\": \"").append(project.getTeamSize()).append("\",\n");
            text.append("    \"duration\": \"").append(project.getDuration()).append("\",\n");
            text.append("    \"budget\": \"").append(project.getBudget()).append("\",\n");
            text.append("    \"lead\": \"").append(clean(project.getLeadName())).append("\",\n");
            text.append("    \"extra\": \"").append(clean(extra)).append("\",\n");
            text.append("    \"tasks\": [\n");
            List<Task> tasks = project.getTasks();
            for (int t = 0; t < tasks.size(); t++) {
                Task task = tasks.get(t);
                text.append("      {\"id\": \"").append(clean(task.getId()));
                text.append("\", \"name\": \"").append(clean(task.getName()));
                text.append("\", \"assignedTo\": \"").append(clean(task.getAssignedTo()));
                text.append("\", \"status\": \"").append(clean(task.getStatus().getLabel()));
                text.append("\", \"hours\": \"").append(task.getHours()).append("\"}");
                if (t < tasks.size() - 1) {
                    text.append(",");
                }
                text.append("\n");
            }
            text.append("    ]\n");
            text.append("  }");
            if (i < list.size() - 1) {
                text.append(",");
            }
            text.append("\n");
        }
        text.append("]\n");

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(text.toString());
        }
    }

    // Returns true when the file was read. Returns false when it is missing or broken.
    public static boolean loadProjects(Path path, ProjectService projectService) {
        if (!Files.exists(path)) {
            System.out.println("✗ Error: Unable to load projects_data.json (File not found)");
            return false;
        }
        try (Stream<String> lines = Files.lines(path)) {
            List<String> all = lines.collect(Collectors.toList());
            List<Project> found = readProjects(all);
            for (Project project : found) {
                projectService.addProject(project);
            }
            return true;
        } catch (Exception ex) {
            System.out.println("✗ Error: Unable to load projects_data.json (malformed data)");
            return false;
        }
    }

    private static List<Project> readProjects(List<String> all) {
        List<Project> found = new ArrayList<>();
        String id = null;
        String name = null;
        String type = null;
        String description = null;
        String lead = null;
        String extra = null;
        int teamSize = 0;
        int duration = 0;
        double budget = 0;
        List<Task> tasks = new ArrayList<>();
        boolean sawList = false;

        for (String raw : all) {
            String line = raw.trim();
            if (line.startsWith("[")) {
                sawList = true;
            }
            if (line.isEmpty() || line.equals("[") || line.equals("]") || line.equals("],")
                    || line.startsWith("\"tasks\"")) {
                continue;
            }
            if (line.contains("\"id\"")) {
                tasks.add(readTask(line));
            } else if (line.equals("{")) {
                id = null;
                name = null;
                type = null;
                description = null;
                lead = null;
                extra = null;
                teamSize = 0;
                duration = 0;
                budget = 0;
                tasks = new ArrayList<>();
            } else if (line.startsWith("}")) {
                if (id != null) {
                    found.add(buildProject(id, name, type, description, teamSize, duration, budget, lead, extra, tasks));
                }
            } else if (line.contains("\"projectId\"")) {
                id = textValue(line);
            } else if (line.contains("\"name\"")) {
                name = textValue(line);
            } else if (line.contains("\"type\"")) {
                type = textValue(line);
            } else if (line.contains("\"description\"")) {
                description = textValue(line);
            } else if (line.contains("\"teamSize\"")) {
                teamSize = Integer.parseInt(textValue(line));
            } else if (line.contains("\"duration\"")) {
                duration = Integer.parseInt(textValue(line));
            } else if (line.contains("\"budget\"")) {
                budget = Double.parseDouble(textValue(line));
            } else if (line.contains("\"lead\"")) {
                lead = textValue(line);
            } else if (line.contains("\"extra\"")) {
                extra = textValue(line);
            }
        }
        if (!sawList) {
            throw new IllegalArgumentException("File is not a project list.");
        }
        return found;
    }

    private static Project buildProject(String id, String name, String type, String description,
                                        int teamSize, int duration, double budget, String lead,
                                        String extra, List<Task> tasks) {
        if (name == null || type == null) {
            throw new IllegalArgumentException("Project is missing a name or type.");
        }
        if (description == null) {
            description = "";
        }
        if (lead == null) {
            lead = "";
        }
        if (extra == null) {
            extra = "";
        }
        Project project;
        if (type.equals("Hardware")) {
            project = new HardwareProject(id, name, description, teamSize, duration, budget, lead, extra);
        } else {
            project = new SoftwareProject(id, name, description, teamSize, duration, budget, lead, extra);
        }
        project.getTasks().addAll(tasks);
        return project;
    }

    private static Task readTask(String line) {
        String id = field(line, "id");
        String name = field(line, "name");
        String assignedTo = field(line, "assignedTo");
        String statusText = field(line, "status");
        int hours = Integer.parseInt(field(line, "hours"));
        TaskStatus status = TaskStatus.fromText(statusText);
        if (status == null) {
            throw new IllegalArgumentException("Bad task status: " + statusText);
        }
        return new Task(id, name, assignedTo, status, hours);
    }

    private static String field(String line, String key) {
        String look = "\"" + key + "\": \"";
        int start = line.indexOf(look);
        if (start < 0) {
            throw new IllegalArgumentException("Missing " + key);
        }
        start = start + look.length();
        int end = line.indexOf('"', start);
        return line.substring(start, end);
    }

    private static String textValue(String line) {
        int colon = line.indexOf(':');
        int first = line.indexOf('"', colon);
        int last = line.lastIndexOf('"');
        if (first < 0 || last <= first) {
            throw new IllegalArgumentException("Bad line: " + line);
        }
        return line.substring(first + 1, last);
    }

    private static String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "").replace("\"", "'").replace("\n", " ");
    }
}
