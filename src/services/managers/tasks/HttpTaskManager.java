package services.managers.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import services.http.clients.KVTaskClient;
import services.managers.util.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {
    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(String url) {
        super();
        gson = Managers.getGson();
        client = new KVTaskClient(url);
        backupAll();
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        String jsonHistory = gson.toJson(history.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())
        );

        client.put("tasks", jsonTasks);
        client.put("epics", jsonEpics);
        client.put("subtasks", jsonSubtasks);
        client.put("history", jsonHistory);
        client.put("currentID", gson.toJson(currentTaskID));
    }

    private void backupAll() {
        List<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<List<Task>>() {
        }.getType());
        List<Task> epics = gson.fromJson(client.load("epics"), new TypeToken<List<Epic>>() {
        }.getType());
        List<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<List<Subtask>>() {
        }.getType());
        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<List<Integer>>() {
        }.getType());
        String currentIdJson = client.load("currentID");

        if (tasks != null)
            backupTasks(tasks);
        if (epics != null)
            backupTasks(epics);
        if (subtasks != null)
            backupTasks(subtasks);
        if (history != null)
            backupHistory(history);

        currentTaskID = currentIdJson == null ? TASK_ID_START_VALUE : gson.fromJson(currentIdJson, Integer.class);
    }

    private void backupTasks(List<? extends Task> tasks) {
        for (Task task : tasks)
            backupTask(task);
    }
}