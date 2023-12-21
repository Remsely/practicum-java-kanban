package services.managers.util;

import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import models.enums.TaskTypes;
import services.managers.histories.HistoryManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVFiles {
    public static List<Integer> historyFromCSV(String csv) {
        List<Integer> history = new ArrayList<>();
        if (!csv.isBlank()) {
            String[] items = csv.split(",");

            for (String item : items)
                history.add(Integer.parseInt(item));
        }
        return history;
    }

    public static Task taskFromCSV(String csv) {
        if (!csv.isBlank()) {
            String[] csvItems = csv.split(",");

            int id = Integer.parseInt(csvItems[0]);
            TaskTypes type = TaskTypes.valueOf(csvItems[1]);
            String name = csvItems[2];
            TaskStatus status = TaskStatus.valueOf(csvItems[3]);
            String description = csvItems[4];
            String startTime = csvItems.length > 5 ? csvItems[5] : "";
            int duration = csvItems.length > 5 ? Integer.parseInt(csvItems[6]) : 0;
            int epicId = csvItems.length > 7 ? Integer.parseInt(csvItems[7]) : -1;

            System.out.println(Arrays.toString(csvItems));

            switch (type) {
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    return epic;
                case SUBTASK:
                    Subtask subtask = new Subtask(epicId, name, description, status);
                    subtask.setId(id);
                    subtask.setStartTime(startTime.isBlank() ? null : LocalDateTime.parse(startTime));
                    subtask.setDuration(duration);
                    return subtask;
                case TASK:
                    Task task = new Task(name, description, status);
                    task.setId(id);
                    task.setStartTime(startTime.isBlank() ? null : LocalDateTime.parse(startTime));
                    task.setDuration(duration);
                    return task;
            }
        }
        return null;
    }

    public static String historyToCSV(HistoryManager manager) {
        StringBuilder historyCSV = new StringBuilder();

        for (Task task : manager.getHistory()) {
            historyCSV.append(task.getId()).append(",");
        }

        if (!(historyCSV.length() == 0)) {
            historyCSV.deleteCharAt(historyCSV.length() - 1);
        }

        return historyCSV.toString();
    }

    public static String taskToCSV(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%d,",
                task.getId(),
                TaskTypes.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime() == null ? "" : task.getStartTime().toString(),
                task.getDuration()
        );
    }

    public static String taskToCSV(Subtask subtask) {
        return String.format("%d,%s,%s,%s,%s,%s,%d,%d",
                subtask.getId(),
                TaskTypes.SUBTASK,
                subtask.getName(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getStartTime() == null ? "" : subtask.getStartTime().toString(),
                subtask.getDuration(),
                subtask.getEpicID()
        );
    }

    public static String taskToCSV(Epic epic) {
        return String.format("%d,%s,%s,%s,%s,,,",
                epic.getId(),
                TaskTypes.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription()
        );
    }

    public static String getCSVAttrs() {
        return "id,type,name,status,description,startTime,duration,epic";
    }
}