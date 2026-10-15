# Lab 2 implementation plan

Lab 2 keeps every Lab 1 feature and adds four layers: a cleaner structure, custom exceptions, JUnit tests, and a Git branch workflow. Data stays in arrays. There is no database.

Do the phases in order. Each phase must still run the Lab 1 menus.

## What already exists

Lab 1 is a working console app.

| Area | Already in the project |
| --- | --- |
| Models | `Project`, `SoftwareProject`, `HardwareProject`, `Task`, `TaskStatus`, `User`, `AdminUser`, `RegularUser`, `StatusReport` |
| Contracts | `Completable` with `isCompleted()` |
| Services | `ProjectService`, `TaskService`, `ReportService`, `UserService` |
| Menu | `ConsoleMenu` and `ValidationUtils` (asks again when input is bad) |
| Startup data | `SampleDataLoader` loads 2 users and 5 projects |
| Completion | `Project.calculateCompletionPercentage()` and `ReportService.calculateAverageCompletion()` |
| Docs | `README.md`, `docs/designDecisions.md`, class diagram |
| Git | The repository already exists and points at GitHub. Do not run `git init` again. |

Keep these Lab 1 rules while adding Lab 2:

- IDs stay `PRJ001`, `TSK001`, and `USR001`. They are generated. Do not switch to `P001` or `T001`.
- Constructors stay as they are. `SoftwareProject` still takes name, description, team size, duration, budget, lead, and technology. The JUnit snippet in the lab brief is only an example. Tests must call the real constructors.
- Arrays stay. Do not introduce `ArrayList`.

## What is not implemented yet

| Lab 2 item | Status | Phase |
| --- | --- | --- |
| SOLID split into services and utils | Done in code | Phase 1 |
| Custom exceptions | Done on branch `feature/exceptions` | Phase 2 |
| try-catch around menu input | Done. The menu catches the exception and stays open | Phase 2 |
| JUnit 5 tests | Missing. There is no `test/` folder | Phase 3 |
| Feature branch, then merge | Branch `feature/refactor` holds Phase 1. Not merged yet | Phase 4 |
| README section on Git workflow | Not written yet | Phase 4 |

## Phase 1 — Refactor existing code

Goal: the same behavior, with clearer classes.

This phase is on branch `feature/refactor`. The behavior stays the same. Each class has one easy job:

- `Main` builds the services and starts the menu.
- `ConsoleMenu` only shows screens and reads choices.
- `ProjectService` stores projects. `TaskService` adds, updates, and removes tasks. `UserService` stores users. `ReportService` prints the average.
- `SampleDataLoader` fills the sample users, projects, and tasks.
- `Project` keeps its fields, its task array, and `calculateCompletionPercentage()`. Software and hardware projects only add their own extra line.
- A new project type can be added by extending `Project`. The menu does not check the class with `instanceof`.

Catalog filters use a plain loop. Task update and task remove each go through one small helper.

Before leaving this phase, run the app and walk the old menus: add a project, browse the catalog, add a task, update a status, open the status report, and switch user.

Do not add exceptions or tests in this phase.

## Phase 2 — Exception handling

Add these classes under `src/utils/exceptions/`. Each one extends `Exception`, so callers must catch it.

| Class | When to throw it |
| --- | --- |
| `InvalidInputException` | Empty text, a bad menu number, a bad email, a bad role, a budget that is not greater than 0, or a team size or duration that is not greater than 0 |
| `ProjectNotFoundException` | The typed project id is not in the catalog |
| `TaskNotFoundException` | The typed task id is not on that project |
| `EmptyProjectException` | Someone asks for a completion result on a project that has zero tasks |

`ValidationUtils` is the only place that decides an input is illegal. It throws. `ConsoleMenu` catches.

Every prompt follows the same shape:

```java
try {
    // read input and call the service
} catch (ProjectNotFoundException ex) {
    System.out.println("❌ Error: ProjectNotFoundException – " + ex.getMessage());
    System.out.println("Please try again.");
} finally {
    // stay in the loop; the exception must not end the program
}
```

A missing project or task prints the error and returns to that same menu. A negative budget prints the error and asks for the budget again.

Payroll Portal can still appear in the catalog with zero tasks. Throw `EmptyProjectException` only when the user asks that project for a completion percentage.

## Phase 3 — JUnit 5

Create `test/` with three classes:

- `ProjectTests`: no tasks is the empty case, one completed task out of two is `50.0`, and all tasks completed is `100.0`.
- `TaskTests`: `updateStatus` changes Pending to Completed, and a missing id throws `TaskNotFoundException`.
- `ValidationTests`: a budget of `0` or below throws `InvalidInputException`.

Use `@Test` and `@BeforeEach`. Assert with `assertEquals` and `assertThrows`. Build each project with the real constructor, then call `calculateCompletionPercentage()`.

## Phase 4 — Git version control

The repository and the GitHub remote already exist. Versioning here means commits, not a release number such as `1.0`.

Use one branch per topic:

1. `feature/refactor` for the Phase 1 structure.
2. `feature/exceptions` for Phase 2.
3. `feature/testing` for Phase 3.

For each topic, the order is: branch, commit, then merge into `main`.

```bash
git checkout main
git checkout -b feature/refactor
git add .
git commit -m "Refactor: split users, display, and sample data out of the menu"
git checkout main
git merge feature/refactor
```

Repeat that shape for exceptions and tests. The lab asks for at least three commits and one merged feature branch. The lab’s example branch for validation is `feature/task-validation`.

**Merge** joins the branch back and keeps the real story: the work happened on the side, then landed on `main`. This is the choice the lab grades. It does not rewrite commits.

**Rebase** is only for a private branch, and only before anyone else has that branch. If `main` moved while you were still working, rebase replays your commits on top of the new `main` so the history is a straight line:

```bash
git checkout feature/exceptions
git rebase main
```

Do that before the merge. Do not rebase after the branch has been pushed for someone else to use. Rebase changes commit ids. Merge does not.

`main` stays the last version that is known to work. Each branch is one lab phase, so a broken exception experiment does not sit on top of the refactor until you merge it. The commit messages are the version history.

## Phase order

1. Phase 1 — structure only. Done in the source.
2. Phase 2 — exceptions, and a menu that catches them. Done on branch `feature/exceptions`.
3. Phase 3 — at least three passing JUnit tests.
4. Phase 4 — branch, commit, merge, push, and a short Git section in the README.
