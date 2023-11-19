package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import services.managers.histories.HistoryManager;
import services.managers.util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager history;
    private int currentTaskID;

    public InMemoryTaskManager() {
        currentTaskID = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getTasks() {
        System.out.println("\nПолучение списка задач Task...");

        List<Task> tasksList = new ArrayList<>(tasks.values());

        System.out.println(tasksList + "\n");

        return tasksList;
    }

    @Override
    public List<Epic> getEpics() {
        System.out.println("\nПолучение списка задач Epic...");

        List<Epic> epicsList = new ArrayList<>(epics.values());

        System.out.println(epicsList + "\n");

        return epicsList;
    }

    @Override
    public List<Subtask> getSubtasks() {
        System.out.println("\nПолучение списка задач Subtask...");

        List<Subtask> subtasksList = new ArrayList<>(subtasks.values());

        System.out.println(subtasksList + "\n");

        return subtasksList;
    }

    @Override
    public void removeAllTasks() {
        System.out.println("\nУдаление всех задач...");
        tasks.clear();
        epics.clear();
        subtasks.clear();
        currentTaskID = 0;
        System.out.println("Удаление завершено!\n");
    }

    @Override
    public Task getTaskByID(int id) {
        System.out.println("\nПолучение задачи по индексу (id = " + id + ")...");
        Task task = tasks.getOrDefault(id, null);

        if (task == null)
            printIndexErrorToConsole(id);
        else
            history.add(task);

        System.out.println(task + "\n");
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        System.out.println("\nПолучение задачи по индексу (id = " + id + ")...");
        Epic epic = epics.getOrDefault(id, null);

        if (epic == null)
            printIndexErrorToConsole(id);
        else
            history.add(epic);

        System.out.println(epic + "\n");
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        System.out.println("\nПолучение задачи по индексу (id = " + id + ")...");
        Subtask subtask = subtasks.getOrDefault(id, null);

        if (subtask == null)
            printIndexErrorToConsole(id);
        else
            history.add(subtask);

        System.out.println(subtask + "\n");
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        System.out.println("\nСоздание задачи...");

        task.setId(currentTaskID);
        tasks.put(currentTaskID++, task);

        System.out.println(task + "\n");
    }

    @Override
    public void createTask(Subtask subtask) {
        System.out.println("\nСоздание задачи...");

        int epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);

        subtask.setId(currentTaskID);
        subtasks.put(currentTaskID++, subtask);

        epic.addSubtaskID(subtask.getId());
        epic.setStatus(calculateEpicStatus(epicID));

        System.out.println(subtask + "\n");
    }

    @Override
    public void createTask(Epic epic) {
        System.out.println("\nСоздание задачи...");

        epic.setId(currentTaskID);
        epics.put(currentTaskID++, epic);

        System.out.println(epic + "\n");
    }

    @Override
    public void updateTask(Task task) {
        System.out.println("\nОбновление задачи (id = " + task.getId() + ")...");
        tasks.put(task.getId(), task);
        System.out.println(task + "\n");
    }

    @Override
    public void updateTask(Subtask subtask) {
        System.out.println("\nОбновление задачи (id = " + subtask.getId() + ")...");

        int epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);

        subtasks.put(subtask.getId(), subtask);
        epic.setStatus(calculateEpicStatus(epicID));

        System.out.println(subtask + "\n");
    }

    @Override
    public void removeTaskByID(int id) {
        System.out.println("\nУдаление задачи (id = " + id + ")...");

        if (tasks.containsKey(id)) {
            tasks.remove(id);
            history.remove(id);
        } else if (epics.containsKey(id)) {
            removeEpicByID(id);
        } else if (subtasks.containsKey(id)) {
            removeSubtaskByID(id);
        } else {
            printIndexErrorToConsole(id);
            return;
        }

        System.out.println("Задача удалена успешно!\n");
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("\nПолучение истории обращения к задачам...");

        List<Task> currentHistory = history.getHistory();
        System.out.println(currentHistory + "\n");

        return currentHistory;
    }

    private void printIndexErrorToConsole(int id) {
        System.out.println("========================================================");
        System.out.println("Ошибка! Попытка обращения к несущеcтвующему индексу: " + id + "!");
        System.out.println("========================================================\n");
    }

    private void removeEpicByID(int id) {
        Epic epic = epics.get(id);

        for (Integer subtaskID : epic.getSubtasksIDs()) {
            subtasks.remove(subtaskID);
            history.remove(subtaskID);
        }
        history.remove(id);
    }

    private void removeSubtaskByID(int id) {
        int epicID = subtasks.get(id).getEpicID();
        Epic epic = epics.get(epicID);

        epic.removeSubtask(id);
        subtasks.remove(id);
        history.remove(id);

        if (epic.getSubtasksIDs().isEmpty())
            epics.remove(epicID);
    }

    private ArrayList<Subtask> getEpicSubtasksByID(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = epics.get(epicId);

        for (Integer id : epic.getSubtasksIDs()) {
            subtasks.add(this.subtasks.get(id));
        }

        return subtasks;
    }

    private TaskStatus calculateEpicStatus(int epicID) {
        ArrayList<Subtask> subtasks = getEpicSubtasksByID(epicID);

        if (subtasks.isEmpty()) return TaskStatus.NEW;

        int newCount = 0;
        int doneCount = 0;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.NEW) newCount++;
            else if (subtask.getStatus() == TaskStatus.DONE) doneCount++;
            else return TaskStatus.IN_PROGRESS;

            if (newCount != 0 && doneCount != 0) return TaskStatus.IN_PROGRESS;
        }

        if (newCount != 0) return TaskStatus.NEW;
        else return TaskStatus.DONE;
    }

    @Override
    public String toString() {
        return "\nТекущее состояние Manager:\n" +
                "InMemoryTaskManager {" + '\n' +
                "currentTaskID=" + currentTaskID + ",\n" +
                "tasks=" + tasks + ",\n" +
                "epics=" + epics + ",\n" +
                "subtasks=" + subtasks + '\n' +
                "}\n";
    }
}
