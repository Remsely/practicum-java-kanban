import models.business.Epic;
import models.business.Subtask;
import models.business.Task;
import models.enums.TaskStatus;
import services.manager.InMemoryTaskManager;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        manager.createTask(new Task(0, "Задача 1", "Описание задачи 1", TaskStatus.NEW));
        manager.createTask(new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.NEW));

        manager.createTask(new Epic(2, "Эпик 1", "Описание эпика 1"));
        manager.createTask(new Subtask(2, 3, "Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW));
        manager.createTask(new Subtask(2, 4, "Подзадача 2", "Описание подзадачи 2", TaskStatus.NEW));

        manager.createTask(new Epic(5, "Эпик 2", "Описание эпика 2"));
        manager.createTask(new Subtask(5, 6, "Подзадача 3", "Описание подзадачи 3", TaskStatus.NEW));

        System.out.println(manager);

        Task task1 = manager.getTaskByID(0);
        Task task2 = manager.getTaskByID(1);
        Task task3 = manager.getEpicByID(2);
        Task task4 = manager.getSubtaskByID(6);

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");

        Task task5 = manager.getSubtaskByID(3);
        Task task6 = manager.getSubtaskByID(4);
        Task task7 = manager.getEpicByID(5);
        task4 = manager.getSubtaskByID(6);

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");

        task1 = manager.getTaskByID(0);
        task2 = manager.getTaskByID(1);
        task3 = manager.getEpicByID(2);
        task4 = manager.getSubtaskByID(6);

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");

        manager.updateTask(new Task(0, "Задача 1", "Описание задачи 1", TaskStatus.IN_PROGRESS));
        manager.updateTask(new Task(1, "Задача 2", "Описание задачи 2", TaskStatus.DONE));

        manager.updateTask(new Subtask(2, 3, "Подзадача 1", "Опиcание подзадачи 1", TaskStatus.IN_PROGRESS));
        manager.updateTask(new Subtask(2, 4, "Подзадача 2", "Опиcание подзадачи 2", TaskStatus.DONE));

        manager.updateTask(new Subtask(5, 6, "Подзадача 3", "Опиcание подзадачи 3", TaskStatus.DONE));

        System.out.println(manager);

        manager.removeTaskByID(4);
        manager.removeTaskByID(1);

        System.out.println(manager);

        manager.removeTaskByID(2);
        manager.removeTaskByID(0);

        System.out.println(manager);

        manager.removeTaskByID(6);

        System.out.println(manager);

        manager.removeTaskByID(6);
        manager.removeTaskByID(2);
        manager.removeTaskByID(0);

        System.out.println(manager);

        System.out.println("\n" + manager.getHistory().size() + " : " + manager.getHistory() + "\n");
    }
}
