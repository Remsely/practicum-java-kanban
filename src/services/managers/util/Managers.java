package services.managers.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import services.managers.adapters.LocalDateTimeJsonAdapter;
import services.managers.histories.HistoryManager;
import services.managers.histories.InMemoryHistoryManager;
import services.managers.tasks.FileBackedTaskManager;
import services.managers.tasks.HttpTaskManager;
import services.managers.tasks.TaskManager;

import java.time.LocalDateTime;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078/");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFromFile(String path) {
        return new FileBackedTaskManager(path);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonAdapter())
                .create();
    }
}