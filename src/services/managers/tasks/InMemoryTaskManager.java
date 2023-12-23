package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import services.managers.histories.HistoryManager;
import services.managers.util.CSVFiles;
import services.managers.util.Managers;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final TreeMap<Integer, Task> prioritizedTasks;
    protected final HistoryManager history;
    protected int currentTaskID;

    public InMemoryTaskManager() {
        currentTaskID = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistory();
        prioritizedTasks = new TreeMap<>(getPrioritizedIdsComparator());
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
    public void clear() {
        System.out.println("\nУдаление всех задач...");

        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
        currentTaskID = 0;

        for (Task task : history.getHistory())
            history.remove(task.getId());

        System.out.println("Удаление завершено!\n");
    }

    @Override
    public Task getTask(int id) {
        System.out.println("\nПолучение задачи по индексу (id = " + id + ")...");
        Task task = tasks.getOrDefault(id, null);

        if (task == null)
            printIndexErrorToConsole(id);
        else
            history.add(task);

        System.out.println(task + "\n");
        return task != null ? new Task(task) : null;
    }

    @Override
    public Epic getEpic(int id) {
        System.out.println("\nПолучение задачи по индексу (id = " + id + ")...");
        Epic epic = epics.getOrDefault(id, null);

        if (epic == null)
            printIndexErrorToConsole(id);
        else
            history.add(epic);

        System.out.println(epic + "\n");
        return epic != null ? new Epic(epic) : null;
    }

    @Override
    public Subtask getSubtask(int id) {
        System.out.println("\nПолучение задачи по индексу (id = " + id + ")...");
        Subtask subtask = subtasks.getOrDefault(id, null);

        if (subtask == null)
            printIndexErrorToConsole(id);
        else
            history.add(subtask);

        System.out.println(subtask + "\n");
        return subtask != null ? new Subtask(subtask) : null;
    }

    @Override
    public int add(Task task) {
        System.out.println("\nСоздание задачи...");

        if (validateIntersectionsInTime(task.getStartTime(), task.getEndTime())) {
            task.setId(currentTaskID);

            tasks.put(currentTaskID, task);
            prioritizedTasks.put(currentTaskID, task);

            currentTaskID++;

            System.out.println(task + "\n");
            return task.getId();
        }
        printIntersectionErrorToConsole();
        return -1;
    }

    @Override
    public int add(Subtask subtask) {
        System.out.println("\nСоздание задачи...");

        if (validateIntersectionsInTime(subtask.getStartTime(), subtask.getEndTime())) {
            int epicID = subtask.getEpicID();
            Epic epic = epics.get(epicID);

            subtask.setId(currentTaskID);
            subtasks.put(currentTaskID, subtask);
            prioritizedTasks.put(currentTaskID, subtask);

            epic.addSubtaskID(subtask.getId());
            epic.setStatus(calculateEpicStatus(epicID));
            setEpicTimes(epicID);

            currentTaskID++;

            System.out.println(subtask + "\n");
            return subtask.getId();
        }
        printIntersectionErrorToConsole();
        return -1;
    }

    @Override
    public int add(Epic epic) {
        System.out.println("\nСоздание задачи...");

        epic.setId(currentTaskID);
        epics.put(currentTaskID++, epic);

        System.out.println(epic + "\n");
        return epic.getId();
    }

    @Override
    public boolean update(int id, Task task) {
        System.out.println("\nОбновление задачи (id = " + id + ")...");

        if (validateIntersectionsInTime(task.getStartTime(), task.getEndTime())) {
            if (tasks.containsKey(id)) {
                task.setId(id);

                updateTasksInMaps(id, task);

                System.out.println(task + "\n");
                return true;
            }
            printIndexErrorToConsole(id);
            return false;
        }
        printIntersectionErrorToConsole();
        return false;
    }

    @Override
    public boolean update(int id, Subtask subtask) {
        System.out.println("\nОбновление задачи (id = " + id + ")...");

        if (validateIntersectionsInTime(subtask.getStartTime(), subtask.getEndTime())) {
            if (subtasks.containsKey(id)) {
                int epicID = subtask.getEpicID();
                Epic epic = epics.get(epicID);

                subtask.setId(id);

                updateTasksInMaps(id, subtask);

                epic.setStatus(calculateEpicStatus(epicID));
                setEpicTimes(epicID);

                System.out.println(subtask + "\n");
                return true;
            }
            printIndexErrorToConsole(id);
            return false;
        }
        printIntersectionErrorToConsole();
        return false;
    }

    @Override
    public boolean remove(int id) {
        System.out.println("\nУдаление задачи (id = " + id + ")...");

        if (tasks.containsKey(id)) {
            removeTaskByID(id);
        } else if (epics.containsKey(id)) {
            removeEpicByID(id);
        } else if (subtasks.containsKey(id)) {
            removeSubtaskByID(id);
        } else {
            printIndexErrorToConsole(id);
            return false;
        }

        System.out.println("Задача удалена успешно!\n");
        return true;
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("\nПолучение истории обращения к задачам...");

        List<Task> currentHistory = history.getHistory();

        System.out.println(currentHistory + "\n");

        return currentHistory;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        System.out.println("\nПолучение списка задач в порядке приоритета...");

        List<Task> prioritizedList = new ArrayList<>(prioritizedTasks.values());

        System.out.println(prioritizedList + "\n");

        return prioritizedList;
    }

    // Насколько вообще хорошая идея хранить задачи в TreeSet таким образом? Я выбрал такой подход из-за простоты
    // обновления задач.
    // И еще вопрос. Стоит ли добавлять содержимое lib в git? По умолчанию IDEA это не делает.
    private Comparator<Integer> getPrioritizedIdsComparator() {
        Comparator<Task> taskPrioriryComparator = Comparator
                .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getName);

        return (id1, id2) -> {
            Task task1 = tasks.getOrDefault(id1, subtasks.get(id1));
            Task task2 = tasks.getOrDefault(id2, subtasks.get(id2));

            if (task1 == null && task2 == null)
                return 0;
            else if (task1 == null)
                return 1;
            else if (task2 == null)
                return -1;

            return taskPrioriryComparator.compare(task1, task2);
        };
    }

    private boolean validateIntersectionsInTime(LocalDateTime newStartTime, LocalDateTime newEndTime) {
        if (newStartTime == null)
            return true;

        for (Task task : prioritizedTasks.values()) {
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();

            if (startTime == null)
                break;

            if (newEndTime.isBefore(startTime))
                break;

            if ((newStartTime.isAfter(startTime) && newStartTime.isBefore(endTime) || newStartTime.isEqual(startTime))
                    || (newEndTime.isBefore(endTime) && newEndTime.isAfter(startTime) || newEndTime.isEqual(endTime))
                    || (newStartTime.isBefore(startTime) && newEndTime.isAfter(endTime))) {
                return false;
            }
        }
        return true;
    }

    private void updateTasksInMaps(int id, Task task) {
        prioritizedTasks.remove(id);

        if (task instanceof Subtask)
            subtasks.put(id, (Subtask) task);
        else
            tasks.put(id, task);

        prioritizedTasks.put(id, task);
    }

    private void removeTaskByID(int id) {
        prioritizedTasks.remove(id);
        tasks.remove(id);
        history.remove(id);
    }

    private void removeEpicByID(int id) {
        Epic epic = epics.get(id);

        for (Integer subtaskID : epic.getSubtasksIDs()) {
            subtasks.remove(subtaskID);
            history.remove(subtaskID);
        }
        epics.remove(id);
        history.remove(id);
    }

    private void removeSubtaskByID(int id) {
        int epicID = subtasks.get(id).getEpicID();
        Epic epic = epics.get(epicID);

        epic.removeSubtask(id);
        epic.setStatus(calculateEpicStatus(epicID));
        setEpicTimes(epicID);

        prioritizedTasks.remove(id);
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

    protected TaskStatus calculateEpicStatus(int epicID) {
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

    protected int calculateEpicDuration(int epicID) {
        ArrayList<Subtask> subtasks = getEpicSubtasksByID(epicID);

        if (subtasks.isEmpty()) return 0;

        int duration = 0;

        for (Subtask subtask : subtasks) {
            int subtaskDuration = subtask.getDuration();
            if (subtaskDuration != 0)
                duration += subtask.getDuration();
        }
        return duration;
    }

    protected void setEpicTimes(int epicID) {
        ArrayList<Subtask> subtasks = getEpicSubtasksByID(epicID);

        if (subtasks.isEmpty()) return;

        boolean hasTimedSubtasks = false;
        LocalDateTime minStartTime = LocalDateTime.MAX;
        LocalDateTime maxEndTime = LocalDateTime.MIN;

        for (Subtask subtask : subtasks) {
            int subtaskDuration = subtask.getDuration();
            LocalDateTime startTime = subtask.getStartTime();

            if (subtaskDuration != 0 && startTime != null) {
                hasTimedSubtasks = true;

                if (startTime.isBefore(minStartTime))
                    minStartTime = startTime;

                LocalDateTime endTime = subtask.getEndTime();

                if (endTime.isAfter(maxEndTime))
                    maxEndTime = endTime;
            }
        }

        if (hasTimedSubtasks) {
            Epic epic = epics.get(epicID);
            epic.setStartTime(minStartTime);
            epic.setEndTime(maxEndTime);
            epic.setDuration(calculateEpicDuration(epicID));
        }
    }

    private void printIndexErrorToConsole(int id) {
        System.out.println("========================================================");
        System.out.println("Ошибка! Попытка обращения к несущеcтвующему индексу: " + id + "!");
        System.out.println("========================================================\n");
    }

    private void printIntersectionErrorToConsole() {
        System.out.println("===================================================================");
        System.out.println("Ошибка обновления задачи (пересечение во времени с другой задачей)!");
        System.out.println("===================================================================\n");
    }

    @Override
    public String toString() {
        return "\nТекущее состояние Manager:\n" +
                "InMemoryTaskManager {" + '\n' +
                "currentTaskID=" + currentTaskID + ",\n" +
                "tasks=" + tasks + ",\n" +
                "epics=" + epics + ",\n" +
                "subtasks=" + subtasks + '\n' +
                "history=[" + CSVFiles.historyToCSV(history) + "]\n" +
                "}\n";
    }
}