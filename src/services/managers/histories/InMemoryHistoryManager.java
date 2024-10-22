package services.managers.histories;

import models.business.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public boolean remove(int id) {
        return history.removeNode(id);
    }

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    private static class CustomLinkedList {
        private final Map<Integer, Node> nodeTable = new HashMap<>();
        private Node head = null;
        private Node tail = null;

        void linkLast(Task task) {
            final Node l = tail;
            final Node newNode = new Node(l, task, null);

            tail = newNode;

            if (l == null)
                head = newNode;
            else
                l.next = newNode;

            removeNode(task.getId());
            nodeTable.put(task.getId(), newNode);
        }

        List<Task> getTasks() {
            List<Task> tasksList = new ArrayList<>();
            Node currentNode = head;

            while (currentNode != null) {
                tasksList.add(currentNode.data);
                currentNode = currentNode.next;
            }
            return tasksList;
        }

        boolean removeNode(int taskId) {
            if (nodeTable.containsKey(taskId)) {
                Node oldNode = nodeTable.get(taskId);

                Node oldNodeNext = oldNode.next;
                Node oldNodePrev = oldNode.prev;

                if (oldNode.equals(head))
                    head = oldNodeNext;

                if (oldNode.equals(tail))
                    tail = oldNodePrev;

                if (oldNodePrev != null)
                    oldNodePrev.next = oldNodeNext;

                if (oldNodeNext != null)
                    oldNodeNext.prev = oldNodePrev;

                nodeTable.remove(taskId);
                return true;
            }
            return false;
        }

        private static class Node {
            private final Task data;
            private Node prev;
            private Node next;

            public Node(Node prev, Task data, Node next) {
                this.data = data;
                this.prev = prev;
                this.next = next;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Node node = (Node) o;
                return Objects.equals(data, node.data) && Objects.equals(prev, node.prev) && Objects.equals(next, node.next);
            }

            @Override
            public int hashCode() {
                return Objects.hash(data, prev, next);
            }
        }
    }
}