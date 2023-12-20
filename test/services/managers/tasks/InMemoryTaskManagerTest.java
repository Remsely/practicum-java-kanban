package services.managers.tasks;

import models.business.Epic;
import models.business.Subtask;
import models.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void initManager() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void epicStatusShouldBeNewIfItIsEmpty() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        TaskStatus status = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.NEW, status, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeNewIfAllSubtasksIsNew() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.NEW);
        manager.createTask(subtask1);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.NEW, epicStatus, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeDoneIfAllSubtasksIsDone() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.DONE);
        manager.createTask(subtask1);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.DONE, epicStatus, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeInProgressIfSubtasksIsNewOrDone() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epicId, "Subtask 2", "Description", TaskStatus.DONE);

        manager.createTask(subtask1);
        manager.createTask(subtask2);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус эпика.");
    }

    @Test
    public void epicStatusShouldBeInProgressIfAllSubtasksIsInProgress() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = manager.createTask(epic);

        Subtask subtask1 = new Subtask(epicId, "Subtask 1", "Description", TaskStatus.IN_PROGRESS);
        manager.createTask(subtask1);

        TaskStatus epicStatus = manager.getEpicByID(epicId).getStatus();
        assertEquals(TaskStatus.IN_PROGRESS, epicStatus, "Неверный статус эпика.");
    }
}