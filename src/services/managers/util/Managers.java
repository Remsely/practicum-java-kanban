package services.managers.util;

import services.managers.histories.HistoryManager;
import services.managers.histories.InMemoryHistoryManager;
import services.managers.tasks.FileBackedTaskManager;
import services.managers.tasks.InMemoryTaskManager;
import services.managers.tasks.TaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFromFile(String path) {
        return new FileBackedTaskManager(path);
    }
}