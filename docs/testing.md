# Testing guide (for collaborators)

Short guide so the next developer can run, read, and extend our tests without guessing.

## What you need

| Tool | Why |
| --- | --- |
| JDK 21 | Same as the app |
| Maven | Downloads JUnit 5 + Mockito and runs tests |
| IntelliJ (optional) | Click-to-run green arrows next to tests |

Dependencies live in `pom.xml` (`junit-jupiter`, `mockito-junit-jupiter`). You do **not** need the JARs under `lib/` if you use Maven.

## How to run tests

From the project root:

```bash
mvn test
```

Success looks like:

```text
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

In IntelliJ:

1. Open `test/ProjectTests.java`
2. Click the green play icon on the class (all tests) or on one method
3. Check the Run window for green / red results

## Where tests live

```text
test/ProjectTests.java
test/StreamOperationsTest.java
test/FilePersistenceTest.java
test/ConcurrencyTest.java
```

App code stays under `src/` (models, services, utils, `Main.java`).  
Maven is configured with:

- `sourceDirectory` = `src`
- `testSourceDirectory` = `test`

Keep new tests in the top-level `test/` folder so they stay off the `Main` classpath.

## Mental model (read this first)

```text
┌─────────────────────┐
│   ProjectTests      │
└─────────┬───────────┘
          │
          ├── real SoftwareProject  ("Alpha", …)
          ├── real TaskService      (built in @BeforeEach)
          │         │
          │         └── may use @Mock ProjectService
          │                   (only when searching across projects)
          └── sometimes a real ProjectService
                    (createProject test only)
```

**Rule of thumb**

| Situation | Use |
| --- | --- |
| Method takes a `Project` and works on it | Real project + real `TaskService` (mock idle) |
| Method looks up projects via `ProjectService` (e.g. find by id) | Mock `ProjectService` with `when` / `verify` |
| Storing a project in the catalog | Real `ProjectService` |

That is why `addTask` and `calculateCompletion` do **not** stub the mock: `TaskService.addTask(project, …)` never calls `projectService`.

## What each test checks

File: `test/ProjectTests.java`  
Setup (`@BeforeEach`): new `TaskService(mockProjectService)` + new `SoftwareProject("Alpha", …)`.

| Test method | Intent | Real vs mock |
| --- | --- | --- |
| `createProject` | Name, type `Software`, and catalog count after `addProject` | **Real** `ProjectService` (local variable) |
| `addTask` | Task name `Design` and `taskCount == 1` | Real project; mock **not used** |
| `calculateCompletion` | 1 completed + 1 pending → `50.0` | Real project; mock **not used** |
| `missingTaskThrows` | Missing id → `TaskNotFoundException` | **Mock** `getCount()` returns `0` |
| `adminCanDeleteProject` | Admin may update/delete | Real `AdminUser` |
| `regularUserCannotDeleteProject` | Regular user may not | Real `RegularUser` |

### Example: completion (no mock)

```java
taskService.addTask(project, "Design", "", TaskStatus.COMPLETED, 2);
taskService.addTask(project, "Build", "", TaskStatus.PENDING, 3);

assertEquals(50.0, taskService.calculateCompletionPercentage(project));
```

Formula: `completed / total * 100` → `1 / 2 * 100 = 50`.

### Example: missing task (mock used)

```java
when(projectService.getCount()).thenReturn(0);   // pretend catalog is empty

assertThrows(TaskNotFoundException.class,
    () -> taskService.findRequired("TSK999"));

verify(projectService).getCount();               // confirm lookup asked the service
```

`findRequired` walks projects through `projectService`. With count `0`, nothing is found → exception.

## How to add a new test

1. Prefer a new `@Test` method in `ProjectTests`, or a new class under `test/` (e.g. `ValidationTests.java`).
2. Keep names short and behavioral: `calculateCompletion`, `missingTaskThrows`.
3. Arrange → Act → Assert:

```java
@Test
void emptyProjectCompletionIsZero() {
    // Arrange: project from setUp has no tasks yet

    // Act
    double percentage = taskService.calculateCompletionPercentage(project);

    // Assert
    assertEquals(0.0, percentage);
}
```

4. Only add `@Mock` / `when` / `verify` if the code under test calls that dependency.
5. Run `mvn test` before you push.

## Common pitfalls

| Pitfall | Fix |
| --- | --- |
| “Mock is never used” warning / unused stub | You stubbed something `addTask` never calls — remove the stub or test a find/remove path |
| Tests fail after changing constructors | Update `setUp()` — `SoftwareProject` needs name, description, team size, duration, budget, lead, technology |
| Compiling `src` with plain `javac` and picking up tests | Tests live in `test/`, so `javac` of `src` no longer sees them. Use `mvn test` / `mvn compile` |
| Expecting `ArrayList` helpers | Data is still plain arrays + counts |

## Manual checks (app, not JUnit)

Useful when collaborating on menu / exceptions:

| Check | How |
| --- | --- |
| Bad project id | Manage Tasks → Add Task → `PRJ999` → expect `ProjectNotFoundException` message, menu stays open |
| Bad number | Type `abc` at a choice → “Please enter a number.” |
| Completion table | Main menu → View Status Reports |
| Permissions | Switch to regular user → update/remove task should be blocked |

Full scenario write-ups (with examples) are in the README section **Testing the Application**.

## Quick reference

```bash
git clone https://github.com/Sergeb250/Task-Mgt-System-II.git
cd Task-Mgt-System-II
mvn test                          # run all tests
mvn -q compile && java -cp target/classes Main   # run the app
```

Questions about a specific test: open `ProjectTests.java` and match the method name to the table above.
