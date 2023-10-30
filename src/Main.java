import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import services.managers.tasks.InMemoryTaskManager;
import services.managers.util.Managers;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = (InMemoryTaskManager) Managers.getDefault();

        manager.createTask(new Task(0, "Задача 1", "Описание задачи 1", TaskStatus.NEW));
        manager.createTask(new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.NEW));

        manager.createTask(new Epic(2, "Эпик 1", "Описание эпика 1"));
        manager.createTask(new Subtask(2, 3, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW));
        manager.createTask(new Subtask(2, 4, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW));

        manager.createTask(new Epic(5, "Эпик 2", "Описание эпика 2"));
        manager.createTask(new Subtask(5, 6, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW));

        System.out.println(manager + "\n");
        System.out.println(manager.getTasks().size() + " : " + manager.getTasks() + "\n");
        System.out.println(manager.getSubtasks().size() + " : " + manager.getSubtasks() + "\n");
        System.out.println(manager.getEpics().size() + " : " + manager.getEpics() + "\n");

        System.out.println(manager.getTaskByID(0));
        System.out.println(manager.getTaskByID(1));
        System.out.println(manager.getEpicByID(2));
        System.out.println(manager.getSubtaskByID(6));

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");

        System.out.println(manager.getSubtaskByID(3));
        System.out.println(manager.getSubtaskByID(4));
        System.out.println(manager.getEpicByID(5));
        System.out.println(manager.getSubtaskByID(6));

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");

        System.out.println(manager.getSubtaskByID(6));
        System.out.println(manager.getTaskByID(1));
        System.out.println(manager.getEpicByID(2));
        System.out.println(manager.getTaskByID(0));

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");

        manager.updateTask(new Task(0, "Задача 1", "Описание задачи 1", TaskStatus.IN_PROGRESS));
        manager.updateTask(new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.DONE));

        manager.updateTask(new Subtask(2, 3, "Подзадача 1", "Описание подзадачи 1", TaskStatus.IN_PROGRESS));
        manager.updateTask(new Subtask(2, 4, "Подзадача 2", "Описание подзадачи 2", TaskStatus.DONE));

        manager.updateTask(new Subtask(5, 6, "Подзадача 3", "Описание подзадачи 3", TaskStatus.DONE));

        System.out.println(manager + "\n");

        manager.removeTaskByID(4);
        manager.removeTaskByID(1);

        System.out.println(manager + "\n");

        manager.removeTaskByID(2);
        manager.removeTaskByID(0);

        System.out.println(manager + "\n");

        manager.removeAllTasks();

        System.out.println(manager + "\n");

        manager.removeTaskByID(6);
        manager.removeTaskByID(2);
        manager.removeTaskByID(0);

        System.out.println(manager + "\n");

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");
    }
}
