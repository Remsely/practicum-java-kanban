package services.manager;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private int currentTaskID;

    public Manager() {
        currentTaskID = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        currentTaskID = 0;
    }

    private Task getTaskByID(int id) {
        return tasks.get(id);
    }

    private Epic getEpicDyID(int id) {
        return epics.get(id);
    }

    private Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        task.setId(currentTaskID);
        tasks.put(currentTaskID++, task);
    }

    public void createTask(Subtask subtask) {
        int epicID = subtask.getEpicID();
        Epic epic = getEpicDyID(epicID);

        subtask.setId(currentTaskID);
        subtasks.put(currentTaskID++, subtask);

        epic.addSubtaskID(subtask.getId());
        epic.setStatus(calculateEpicStatus(epicID));
    }

    public void createTask(Epic epic) {
        epic.setId(currentTaskID);
        epics.put(currentTaskID++, epic);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateTask(Subtask subtask) {
        int epicID = subtask.getEpicID();
        Epic epic = getEpicDyID(epicID);

        subtasks.put(subtask.getId(), subtask);
        epic.setStatus(calculateEpicStatus(epicID));
    }

    public void removeTaskByID(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            removeEpicByID(id);
        } else if (subtasks.containsKey(id)) {
            removeSubtaskByID(id);
        }
    }

    private void removeEpicByID(int id) {
        Epic epic = getEpicDyID(id);

        for (Integer subtaskID : epic.getSubtasksIDs())
            subtasks.remove(subtaskID);

        epics.remove(id);
    }

    private void removeSubtaskByID(int id) {
        int epicID = getSubtaskByID(id).getEpicID();
        Epic epic = getEpicDyID(epicID);

        epic.removeSubtask(id);
        subtasks.remove(id);

        if (epic.getSubtasksIDs().isEmpty())
            epics.remove(epicID);
    }

    private ArrayList<Subtask> getEpicSubtasksByID(int epicId) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        Epic epic = getEpicDyID(epicId);

        for (Integer id : epic.getSubtasksIDs()) {
            subtasks.add(getSubtaskByID(id));
        }

        return subtasks;
    }

    private String calculateEpicStatus(int epicID) {
        ArrayList<Subtask> subtasks = getEpicSubtasksByID(epicID);

        if (subtasks.isEmpty()) return "NEW";

        int newCount = 0;
        int doneCount = 0;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus().equals("NEW")) newCount++;
            else if (subtask.getStatus().equals("DONE")) doneCount++;
            else return "IN_PROGRESS";

            if (newCount != 0 && doneCount != 0) return "IN_PROGRESS";
        }

        if (newCount != 0) return "NEW";
        else return "DONE";
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