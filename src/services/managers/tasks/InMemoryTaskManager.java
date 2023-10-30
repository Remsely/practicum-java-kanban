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

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
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
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        currentTaskID = 0;
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.getOrDefault(id, null);

        if (task == null)
            printIndexErrorToConsole(id);
        else
            history.add(task);

        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.getOrDefault(id, null);

        if (epic == null)
            printIndexErrorToConsole(id);
        else
            history.add(epic);

        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.getOrDefault(id, null);

        if (subtask == null)
            printIndexErrorToConsole(id);
        else
            history.add(subtask);

        return subtask;
    }

    @Override
    public void createTask(Task task) {
        task.setId(currentTaskID);
        tasks.put(currentTaskID++, task);
    }

    @Override
    public void createTask(Subtask subtask) {
        int epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);

        subtask.setId(currentTaskID);
        subtasks.put(currentTaskID++, subtask);

        epic.addSubtaskID(subtask.getId());
        epic.setStatus(calculateEpicStatus(epicID));
    }

    @Override
    public void createTask(Epic epic) {
        epic.setId(currentTaskID);
        epics.put(currentTaskID++, epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Subtask subtask) {
        int epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);

        subtasks.put(subtask.getId(), subtask);
        epic.setStatus(calculateEpicStatus(epicID));
    }

    @Override
    public void removeTaskByID(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            removeEpicByID(id);
        } else if (subtasks.containsKey(id)) {
            removeSubtaskByID(id);
        } else {
            printIndexErrorToConsole(id);
        }
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

    private void printIndexErrorToConsole(int id) {
        System.out.println("==================================================");
        System.out.println("Попытка обращения к несущеcтвующему индексу: " + id + "!");
        System.out.println("==================================================");
    }

    private void removeEpicByID(int id) {
        Epic epic = epics.get(id);

        for (Integer subtaskID : epic.getSubtasksIDs())
            subtasks.remove(subtaskID);

        epics.remove(id);
    }

    private void removeSubtaskByID(int id) {
        int epicID = subtasks.get(id).getEpicID();
        Epic epic = epics.get(epicID);

        epic.removeSubtask(id);
        subtasks.remove(id);

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
        return "services.manager.Manager {" + '\n' +
                "currentTaskID=" + currentTaskID + ",\n" +
                "tasks=" + tasks + ",\n" +
                "epics=" + epics + ",\n" +
                "subtasks=" + subtasks + '\n' +
                '}';
    }
}
