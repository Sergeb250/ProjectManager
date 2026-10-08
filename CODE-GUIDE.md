# Code guide

This file maps the lab brief to the Java that is actually in this project. Use it when you need to change a screen, a rule, or a calculation. The lab text is in `PROJECTMANAGER.MD`. This file says which method does that job.

## How a change travels

```text
Main.main
  -> ConsoleMenu.start          the numbered menus
       -> ProjectService        the Project[] array
       -> TaskService           add, find, update, remove tasks
       -> ReportService         the completion table and the average
            -> StatusReport     one project's numbers
       -> ValidationUtils       numbers, text, ids, status words
```

Models hold the data. Services walk the arrays. The menu only asks questions and calls those services.

| If you need to change... | Open this file | Method |
| --- | --- | --- |
| A menu number or a menu title | `src/utils/ConsoleMenu.java` | `start`, `manageProjects`, `manageTasks`, `browseCatalog` |
| What is asked when creating a project | `src/utils/ConsoleMenu.java` | `addProject` |
| What is asked when creating a task | `src/utils/ConsoleMenu.java` | `addTask`, `addTaskTo` |
| The project list columns | `src/services/ProjectService.java` | `printTable` |
| One catalog row | `src/models/Project.java` | `displayCatalogRow` |
| The project details screen | `src/models/Project.java` | `displayProject` |
| The task table | `src/models/Project.java` | `printTaskTable` |
| Software-only or hardware-only text | `src/models/SoftwareProject.java` or `HardwareProject.java` | `getType`, `getProjectDetails` |
| Sorting or budget search | `src/services/ProjectService.java` | `sortByName`, `printByBudget` |
| The completion percent | `src/models/Project.java` | `getCompletionPercentage` |
| The all-projects report and the average | `src/services/ReportService.java` | `printReport` |
| Allowed statuses | `src/models/TaskStatus.java` | `fromText` |
| "Invalid option" and "Please enter a number" | `src/utils/ValidationUtils.java` | `readChoiceInRange` |
| The five projects that exist at startup | `src/utils/ConsoleMenu.java` | `loadSampleData` |
| Who may update or delete | `src/models/AdminUser.java` and `RegularUser.java` | `canUpdateAndDelete` |

## What the program does when it starts

`src/Main.java` builds one `ConsoleMenu` and calls `start()`.

`ConsoleMenu` then:

1. Creates Alice Johnson as `AdminUser` (`USR001`) and Bob Smith as `RegularUser` (`USR002`). Alice is the logged-in user.
2. Calls `loadSampleData()`, which fills the project array with five projects and their tasks.
3. Draws the main menu inside a `do-while` loop until the user picks Exit.

The five sample projects are created in this order, so their ids are fixed for a fresh run:

| ID | Name | Type | Tasks | Completion |
| --- | --- | --- | --- | --- |
| PRJ001 | Alpha Tracker | Software | 3 (1 completed) | 33.33% |
| PRJ002 | IoT Sensor Kit | Hardware | 2 (1 completed) | 50.00% |
| PRJ003 | Mobile Checkout | Software | 2 (both completed) | 100.00% |
| PRJ004 | Warehouse Robot | Hardware | 1 (pending) | 0.00% |
| PRJ005 | Payroll Portal | Software | 0 | 0.00% |

Task ids on those projects are `TSK001` through `TSK008`. The next project you add is `PRJ006`. The next task you add is `TSK009`.

To change the sample list, edit `loadSampleData` in `ConsoleMenu`. To change how many projects fit, edit `MAX_PROJECTS` in `ProjectService` (20). To change how many tasks fit on one project, edit `MAX_TASKS` in `Project` (20).

## The menu you actually see

`start()` in `ConsoleMenu` prints this and switches on the number:

```text
1. Manage Projects     -> manageProjects()
2. Manage Tasks        -> manageTasks()
3. View Status Reports -> viewReports()
4. Switch User         -> switchUser()
5. Exit                -> prints Goodbye and ends the do-while
```

Inside Manage Projects:

```text
1. Add Project             -> addProject()
2. Browse Project Catalog  -> browseCatalog()
3. <<Back                  -> return to the main menu
```

Inside Browse Project Catalog:

```text
1. View All Projects       -> projectService.printAll()
2. Software Projects Only  -> projectService.printByType("Software")
3. Hardware Projects Only  -> projectService.printByType("Hardware")
4. Search by Budget Range  -> searchByBudget()
5. <<Back
```

After a list is printed, the catalog asks:

```text
1. View Project Details  -> prompt for a project id, then showProjectDetails()
2. <<Back
```

Inside a project's details:

```text
1. Add New Task        -> addTaskTo(project)
2. Update Task Status  -> updateTaskOn(project)   admin only
3. Remove Task         -> removeTaskOn(project)   admin only
4. <<Back
```

Inside Manage Tasks:

```text
1. Add Task            -> addTask()
2. View Tasks          -> viewTasks()
3. Update Task Status  -> updateTaskStatus()      admin only
4. Remove Task         -> removeTask()            admin only
5. <<Back
```

The lab's later test list uses different numbers (`1` Add Project, `2` View Projects, `3` Add Task, and so on). Those numbers are not the main menu today. The same work is on the submenus above. The test section at the bottom shows the clicks for this menu, and which lines to edit if a test must use the other numbers.

## Workflow 1: Complete task assignment journey

| Step in the brief | What you press | Code |
| --- | --- | --- |
| User logs in | Nothing. Alice is already current | `ConsoleMenu` constructor sets `currentUser = users[0]` |
| Main menu | Shown by `start()` | `src/utils/ConsoleMenu.java` |
| Manage Projects | Main menu `1` | `manageProjects()` |
| Add a project | Manage Projects `1` | `addProject()` builds a `SoftwareProject` or `HardwareProject`, then `projectService.add` |
| Add tasks | Open the project from the catalog, then details `1`. Or main menu `2`, then Manage Tasks `1` | `addTaskTo` / `addTask` then `taskService.add` |
| Update statuses | Details `2`, or Manage Tasks `3` | `updateTaskOn` / `updateTaskStatus` then `task.setStatus` |
| Completion average | Main menu `3` | `viewReports()` -> `reportService.printReport` |
| View the report | Same screen. It prints every project and `AVERAGE COMPLETION` | `ReportService.printReport(ProjectService)` |

Alpha Tracker already exists as `PRJ001`. Adding another project named Alpha Tracker creates a new id. It does not edit `PRJ001`.

`addProject` asks, in order: type, name, description, duration, budget, team size, then technology (software) or equipment (hardware). The lead name is the logged-in user. It is not asked.

## Workflow 2: Project discovery

| Step | What you press | Code |
| --- | --- | --- |
| Browse Projects | Main `1`, then Manage Projects `2` | `browseCatalog()` |
| Filter Software | Catalog `2` | `printByType("Software")` |
| View details | After the list, choose `1`, then type an id such as `PRJ001` | `showProjectDetails` -> `project.displayProject(true)` |
| Add a related task | On the details screen, choose `1` | `addTaskTo(project)` |
| Return to the main menu | Details `4` goes back to the catalog. Catalog `5`, then Manage Projects `3` | each `return` leaves that loop |

`displayProject(true)` prints name, type, team size, budget, description, the type-specific line, the task table, and the completion rate.

Software's extra line comes from `SoftwareProject.getProjectDetails()` (`Technology: ...`). Hardware's extra line comes from `HardwareProject.getProjectDetails()` (`Equipment: ...`). `displayProject` calls `getProjectDetails()` without asking which subclass it is. That is the polymorphism for project details.

## Workflow 3: Task management

| Step | What you press | Code |
| --- | --- | --- |
| Manage Tasks | Main menu `2` | `manageTasks()` |
| View tasks | Manage Tasks `2`, then a project id | `viewTasks()` -> `project.printTaskTable()` |
| Update status | Manage Tasks `3`, then a task id such as `TSK003`, then `Completed` | `updateTaskStatus()` |
| Delete a task | Manage Tasks `4`, then a task id | `removeTask()` -> `project.removeTaskById` |
| Recalculate progress | Open main menu `3`, or open the project details again | percent is computed when it is printed, not stored on the project |

A regular user who picks update or remove sees `Only an admin can update a task.` or `Only an admin can remove a task.` Switch to Bob with main menu `4`, then `2`. Switch back to Alice with `4`, then `1`.

Deleting shifts later tasks one slot left in that project's `Task[]`. The task id is not reused. The next new task still gets the next number.

## Workflow 4: Status reporting

| Step | What you press | Code |
| --- | --- | --- |
| View Status Reports | Main menu `3` | `viewReports()` |
| All projects and percents | Printed immediately | `ReportService.printReport(ProjectService)` |
| Compare and return | The screen then offers `1. <<Back` | `readChoiceInRange(..., 1, 1)` |

There is also `calculateCompletion()` in `ConsoleMenu`. It asks for one project id and calls `reportService.printReport(project)`. Nothing in `start()` calls it. The all-projects report is the one on the menu.

The average is the mean of the project percents. With the five sample projects it is about `36.67%`, because `(33.33 + 50 + 100 + 0 + 0) / 5`.

```java
double[] percentages = new double[count];
// one StatusReport per project, store report.getCompletionPercent()
double average = sum / count;
System.out.printf(Locale.US, "AVERAGE COMPLETION: %.2f%%%n", average);
```

That array is the "store computed percentages temporarily" requirement. It lives only inside `printReport` and is thrown away when the method ends.

## User stories

### US-1.1 Browse projects

Shown by `Project.displayCatalogRow` and `ProjectService.printTable`.

The row prints id, name, type, team size, and budget. The next line prints the description.

```java
System.out.printf(Locale.US, "%-8s | %-20s | %-10s | %-9d | %s%n",
        id, fit(name, 20), getType(), teamSize, formatMoney(budget));
System.out.println("         | Description: " + description);
```

Filter by type: catalog option `2` or `3`, which calls `collectByType`. That method counts matches, builds a new `Project[]` of that size, copies the matches, then `sortByName` sorts the copy. The original `projects` array stays in the order projects were added.

`Project` is abstract. These fields are private: `id`, `name`, `description`, `teamSize`, `duration`, `budget`, `leadName`, plus the `tasks` array. `getProjectDetails()` is abstract. `displayProject()` is concrete. `SoftwareProject` and `HardwareProject` extend `Project`.

Five projects are stored in `ProjectService.projects` by `loadSampleData`.

### US-1.2 Budget range

Catalog option `4` calls `searchByBudget()` in `ConsoleMenu`.

- Minimum and maximum come from `ValidationUtils.readBudget`. A letter prints `Please enter a number.` A negative number is rejected.
- If max is less than min, the menu prints the maximum-budget error and asks again.
- `ProjectService.collectByBudget` keeps a project when `budget >= min && budget <= max`.
- If the result array length is 0, `printByBudget` prints `No projects found.`
- Matches are printed with `System.out.printf`.

### US-2.1 Add a task

`Task` fields: `id`, `name`, `assignedTo`, `hours`, `status`. The id is `TSK` plus a static counter.

```java
this.id = String.format("TSK%03d", nextNumber);
nextNumber++;
```

`Task` implements `Completable`. `isCompleted()` is true only when status is `COMPLETED`.

Each project owns `Task[] tasks` and `taskCount`. `Project.addTask` stores the task in the next open slot.

Duplicate names: `Project.hasTaskName` loops the task array with `equalsIgnoreCase`. `addTaskTo` refuses the name before it asks for status. `TaskService.add` checks again and returns `null` if the name is already there.

The add screens currently pass `""` as the assigned person (`addTask` and `addTaskTo(Project)`). The `assignedTo` field exists. `chooseUser` can list users, but the add screens do not call it. If you are asked to type an assigned person, ask for the text in `addTaskTo` and pass that string into `taskService.add` instead of `""`.

### US-2.2 Update task status

Manage Tasks `3` calls `promptForTask`, which calls `TaskService.findById`. That loops every project and each project's `findTaskById`.

Allowed values are the `TaskStatus` enum: Pending, In Progress, Completed. `ValidationUtils.readStatus` uses `TaskStatus.fromText`. `Done` is rejected with:

```text
❌ Error: Invalid status. Please choose from [Pending, In Progress, Completed].
```

You may type the word or `1`, `2`, `3`.

The update writes through the setter:

```java
public void setStatus(TaskStatus status) {
    this.status = status;
}
```

The menu then prints `✓ Task "..." marked as Completed.`

### US-3.1 Users

`User` is abstract. Fields `id`, `name`, `email`, `role` are private and set in the constructor. The id uses a static counter: `USR001`, `USR002`.

`AdminUser.canUpdateAndDelete()` returns `true`. `RegularUser.canUpdateAndDelete()` returns `false`. `printUserReport()` is overridden in both, so the switch-user list does not use `instanceof`.

The main menu prints `Current User: Alice Johnson (Admin)` from `currentUser.getName()` and `currentUser.getRole()`.

Create a user from main menu `4`, then Create User. Only an admin can do that (`requireAdmin`). The new object is an `AdminUser` or a `RegularUser` and is stored in `User[] users`.

### US-4.1 Completion percent

```java
public double getCompletionPercentage() {
    if (taskCount == 0) {
        return 0.0;
    }
    return (countByStatus(TaskStatus.COMPLETED) * 100.0) / taskCount;
}
```

`countByStatus` loops the task array. A project with no tasks returns `0.0` and does not divide. `StatusReport.print` says `This project has no tasks.` in that case. Percents are printed with `%.2f`, so you see `0.00`, `33.33`, `66.67`, `100.00`.

`PRJ003` Mobile Checkout is the all-complete sample. `PRJ004` Warehouse Robot is the all-pending sample. `PRJ005` Payroll Portal is the zero-task sample.

### US-5.1 Main menu

`start()` uses `Scanner` through `ValidationUtils`, a `switch`, and a `do-while` that stops when the choice is `5`.

```java
do {
    // print options 1 to 5
    choice = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 5);
    switch (choice) { ... }
} while (choice != 5);
```

`10` prints `Invalid option.` and asks again. `abc` prints `Please enter a number.` and asks again. The program stays in the loop.

The brief's acceptance line says options numbered 1–5. That matches this `start()` method.

## Project structure

| Brief file | In this project | Job |
| --- | --- | --- |
| `src/Main.java` | `src/Main.java` | Starts the menu |
| `models/Project.java` | same | Abstract project, task array, percent, catalog row |
| `models/SoftwareProject.java` | same | `getType` returns Software, details return technology |
| `models/HardwareProject.java` | same | `getType` returns Hardware, details return equipment |
| `models/Task.java` | same | One task, `Completable` |
| `models/User.java` | same | Abstract user and `USR` ids |
| `models/RegularUser.java` | same | Cannot update or delete |
| `models/AdminUser.java` | same | Can update or delete |
| `models/StatusReport.java` | same | Snapshot of one project's counts |
| `interfaces/Completable.java` | same | `boolean isCompleted()` |
| `services/ProjectService.java` | same | `Project[]`, search, filter, sort |
| `services/TaskService.java` | same | Add, find, update, remove |
| `services/ReportService.java` | same | Table plus average |
| `utils/ConsoleMenu.java` | same | Every screen |
| `utils/ValidationUtils.java` | same | Input checks |
| `docs/class-diagram.png` | same | Class picture |
| `docs/designDecisions.md` | same | Why the classes are split this way |
| `README.md` | same | How to run |

`TaskStatus.java` is extra. It is the enum the brief allows for valid statuses.

## Where the rules live

**Encapsulation.** Model fields are private. Ids, names, budgets, and hours have getters and no setters. Status is the field that can change, and only through `setStatus(TaskStatus)`.

**Inheritance.** `SoftwareProject` and `HardwareProject` extend `Project`. `AdminUser` and `RegularUser` extend `User`.

**Overriding.** `getType`, `getProjectDetails`, `printUserReport`, `canUpdateAndDelete`, and `isCompleted`.

**Overloading.** `Project.displayProject()` and `displayProject(boolean)`. `ReportService.printReport(ProjectService)` and `printReport(Project)`. `ValidationUtils.readPositiveInt` with two parameter lists. `ConsoleMenu.addTaskTo(Project)` and `addTaskTo(Project, String, String)`.

**Arrays.** `ProjectService.projects`, `Project.tasks`, `ConsoleMenu.users`. Each has a count of used slots. Empty slots at the end are not real data.

**Search.** `findById` and `findTaskById` walk the used slots. For n items that is O(n).

**Sort.** `sortByName` is bubble sort on a copy. Neighbours swap when `compareToIgnoreCase` is greater than 0. For n names that is O(n²). The stored array is not reordered, so `PRJ001` stays in slot 0.

**Remove.** `removeTaskById` shifts later tasks left. That is also O(n).

## Test scenarios on this menu

The brief's test list uses one flat menu. This program uses the 5-option menu plus submenus. Do the test with the clicks in the right column.

| Brief test | Brief number | Do this in the running app | Pass when you see |
| --- | --- | --- | --- |
| 1 View projects | option 2 | Main `1`, Manage Projects `2`, catalog `1` | Ids, names, types, budgets, team size, description, `Total projects: 5` |
| 1 Separate types | same | Catalog `2`, then catalog `3` | Software list, then a hardware list. An empty type prints `None.` |
| 2 Add software | option 1 | Main `1`, Manage Projects `1`, type `1` | `ID: PRJ006 \| Type: Software`, then the new row in catalog `1` |
| 3 Add hardware | option 1 | Type `2` | `Type: Hardware` and the total count one higher than before |
| 4 Add task | option 3 | Main `2`, Manage Tasks `1`, or details `1` | `ID: TSK009` (or the next free task id) under that project |
| 5 View tasks | option 4 | Manage Tasks `2`, id `PRJ001` | `Completed`, `In Progress`, and `Pending` on the status column |
| 5 Empty project | same | Project id `PRJ005` | `No tasks found.` |
| 6 Update status | option 5 | Manage Tasks `3`, id `TSK003`, status `Completed` | The PRJ001 task list shows Write Unit Tests as Completed |
| 7 Completion | option 6 | Main `3` for every project. For one project, the method is `calculateCompletion`, which is not on the menu | `100.00%` for PRJ003, `0.00%` for PRJ004, `0.00%` for PRJ005 |
| 8 Bad menu number | `10` | Type `10` on the main menu | `Invalid option.` and the menu asks again |
| 8 Bad text | a letter | Type `abc` at a menu, or `nope` at duration | `Please enter a number.` and the same question again |
| 9 Ids | create several | Add two projects and two tasks without restarting | `PRJ006` then `PRJ007`, `TSK009` then `TSK010`. No repeats |

If a marker's script really types `2` and expects the project list immediately, change `start()` so case `2` calls `projectService.printAll()`, and move Manage Tasks to another number. The list logic is already `printAll`. Only the menu number would move.

`printAll` currently prints one table sorted by name, with a TYPE column. It does not print the two headings `--- Software Projects ---` and `--- Hardware Projects ---`. Those two lists are catalog options `2` and `3` (`printByType`). If a test requires both headings on one screen, change `printAll` so it calls `printByType("Software")` and then `printByType("Hardware")`, then prints the total.

The task table printed by `printTaskTable` has ID, task name, and status. It does not print hours. Hours are stored on `Task` and `Task.printDetails()` can print them. If a test requires hours on the view-tasks screen, add a column in `printTaskTable`.

## Small edits you are likely to be asked for

**Change a menu title.** Edit the `System.out.println` lines in `start`, `manageProjects`, `browseCatalog`, `showProjectDetails`, or `manageTasks`. If you add or remove a number, change the `1, 5` (or other max) passed to `readChoiceInRange`, and add a `case` in the `switch`.

**Add a field to every project.** Add a `private final` field and a constructor parameter in `Project`, pass it through `super(...)` in both subclasses, and print it in `displayProject` and, if it belongs on the list, in `displayCatalogRow`. Then pass a value from `addProject` and from `loadSampleData`.

**Add a software-only or hardware-only field.** Put it only on that subclass, and return it from `getProjectDetails`. `displayProject` already prints that line. Ask for the value in the matching branch of `addProject`.

**Change the percent.** Edit `getCompletionPercentage`. Anything that prints a percent calls this method, so the catalog, the details screen, and the report stay in step.

**Add a status.** Add a constant in `TaskStatus`, teach `fromText` to recognize the word, and include it in the error sentence in `readStatus`.

**Let a regular user update tasks.** Make `RegularUser.canUpdateAndDelete` return `true`, or stop calling `requireAdmin` in `updateTaskStatus`. Delete uses the same check.

**Assign a person to a task.** In `addTaskTo(Project, String, String)`, before `readStatus`, read a name with `readText` or pick a user with `chooseUser`, and pass that name into `taskService.add`.

**Show hours on the task table.** In `printTaskTable`, add `task.getHours()` to the `printf`. The details method `printDetails` already prints hours if you would rather call that.

**Change id format.** Project ids are built in the `Project` constructor (`PRJ%03d`). Task ids are built in the `Task` constructor (`TSK%03d`). User ids are built in the `User` constructor (`USR%03d`). `ValidationUtils.normalizeId` must use the same prefix or typed ids will be rejected.
