package com.premiumminds.internship.taskscheduler;

import java.util.HashSet;
import java.util.Set;

public class Task {

    private final String id;
    private final int priority;
    private final Set<String> dependencies;
    private TaskStatus status;

    public Task(String id, int priority, Set<String> dependencies) {
        this.id = id;
        this.priority = priority;
        this.dependencies = dependencies;
        this.status = TaskStatus.PENDING;
    }

    /**
     * This constructor receives a task and creates a deep copy of it.
     */
    public Task(Task original) {
        this.id = original.id;
        this.priority = original.priority;

        if (original.dependencies != null) {
            this.dependencies = new HashSet<>(original.dependencies);
        } else {
            this.dependencies = new HashSet<>();
        }

        this.status = original.status;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
