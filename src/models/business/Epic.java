package models.business;

import models.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksIDs;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksIDs = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.name, epic.description, epic.status);
        this.id = epic.id;
        this.startTime = epic.startTime;
        this.duration = epic.duration;
        this.subtasksIDs = epic.subtasksIDs;
        this.endTime = epic.endTime;
    }

    public List<Integer> getSubtasksIDs() {
        return subtasksIDs;
    }

    public void addSubtaskID(int id) {
        subtasksIDs.add(id);
    }

    public void removeSubtask(Integer id) {
        subtasksIDs.remove(id);
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIDs, epic.subtasksIDs) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIDs, endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtasksIDs=" + subtasksIDs +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }
}
