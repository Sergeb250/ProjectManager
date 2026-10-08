# OOP design rationale

This note explains the object-oriented choices in plain language. The class diagram is [class-diagram.png](class-diagram.png).

## How to read the diagram

A white triangle means **extends**. `SoftwareProject` and `HardwareProject` extend `Project`. `AdminUser` and `RegularUser` extend `User`.

A dashed line means **implements**. `Project` and `Task` both implement `Completable`.

A filled diamond means **has**. One `Project` has `0..20` tasks. Each `Task` has one `TaskStatus`. One `StatusReport` summarizes one `Project`.

## Why these classes

`Project` is abstract. Every project has an id, name, description, team size, duration, budget, a project lead, and a task array. You cannot write `new Project(...)`, because a project must be software or hardware.

`SoftwareProject` and `HardwareProject` call the parent constructor, then override two methods:

- `getType()` returns `Software` or `Hardware`
- `getProjectDetails()` returns the extra line for that type (`Technology` or `Equipment`)

`displayProject()` is a concrete method on `Project`. It prints the shared fields and then calls `getProjectDetails()`. The catalog and the details screen both use a `Project` reference. Java runs the subclass method, so a software project shows its technology and a hardware project shows its equipment. That is polymorphism.

`displayProject()` and `displayProject(boolean)` are overloaded. The boolean version also prints the task table. `readPositiveInt` is overloaded the same way: one version uses a default message, the other takes the message for team size, duration, or hours. `ReportService.printReport(ProjectService)` prints every project. `ReportService.printReport(Project)` prints one project.

`User` is abstract. `AdminUser` and `RegularUser` override `printUserReport()` and `canUpdateAndDelete()`. The menu stores both in one `User[]` and calls those methods. It does not check the class with `instanceof`. Update, remove, and create-user ask `canUpdateAndDelete()` first. A regular user can still view and add.

`Task` implements `Completable`. The completion count calls `isCompleted()` instead of comparing a status string in the menu. `Project` implements the same interface: a project is complete only when it has tasks and every task is completed. The allowed statuses are `Pending`, `In Progress`, and `Completed` in the `TaskStatus` enum.

`StatusReport` is built from one project. `ReportService` creates one report per project, stores the percents in a temporary array, and prints the average. `ProjectService` stores the project array, finds a project by id, filters by type or budget, and sorts a copy by name. `TaskService` adds a task, finds a task id, updates the status, and removes a task. `ConsoleMenu` reads the menu. `ValidationUtils` checks numbers, email, empty text, and ids.

## Encapsulation

Fields are private. The id, name, description, team size, duration, budget, lead, technology, and equipment are set in the constructor. Task status can change, but `setStatus` only accepts a `TaskStatus`. The project array stays inside `ProjectService`. The task array stays inside `Project`. The menu asks those classes to add, find, or remove.

## Arrays, search, and sort

Projects live in a `Project[]` with a `projectCount` for how many slots are used. Each project has its own `Task[]` and `taskCount`. Users live in a `User[]`. Empty slots at the end are not real data.

Finding a project id or a task id is a linear search: look at each used slot until the id matches. For n items that is O(n). Each list holds at most 20 items, so that cost is small.

Removing a task shifts later tasks one slot left so the array has no hole. That is also O(n).

The catalog copies one type, or one budget range, into a small array, then sorts that copy by name with bubble sort. Each pass walks the list and swaps neighbours that are out of order. For n names that is O(n²). The original array stays in the order projects were added, so ids still follow `PRJ001`, `PRJ002`, and so on.

Budget search is another linear scan. A project is kept when `budget >= min` and `budget <= max`. If none match, the screen prints `No projects found.`

## Ids

A `static` counter is shared by every object of that class. It only moves forward, including after a task is removed.

- Projects: `PRJ001`, `PRJ002`, ...
- Tasks: `TSK001`, `TSK002`, ...
- Users: `USR001`, `USR002`, ...

Ids stay unique for the whole run. Nothing is saved to disk, so a new run starts again at `PRJ001`. The five sample projects use the first five project ids.

## Completion percent

```text
percent = completed tasks / total tasks * 100
```

The code checks for zero tasks before it divides. The result is printed with two decimal places, for example `0.00%`, `33.33%`, and `100.00%`. The average on the status report is the mean of the project percents, not a single fraction of every task in the system.

## What this version does not do

There is no password, no file saving, and no `ArrayList`. When the program ends, the arrays are gone. Per-user performance summaries are left for a later lab.
