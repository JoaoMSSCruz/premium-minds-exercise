package com.premiumminds.internship.taskscheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskSchedulerService implements ITaskSchedulerService {

    /**
     * This method has a time complexity of O(n^2) and a space complexity of O(n).
     */
    @Override
    public List<Task> getEligibleTasks(Collection<Task> tasks) {
        List<Task> eligibleTasks = new ArrayList<>();

        /* 
         * To optimize time complexity and avoid O(n), all tasks are mapped by their ID.
         * Thus, any dependency lookup is done in O(1).
         */
        Map<String, Task> taskById = new HashMap<>();
        for (Task task : tasks) {
            taskById.put(task.getId(), task);
        }            
 
        for (Task task : tasks) {
            if (task.getStatus() != TaskStatus.PENDING) {
                continue;
            }

            boolean eligible = true; 

            Set<String> taskDependencies = task.getDependencies();

            if (taskDependencies.isEmpty()) { 
                eligibleTasks.add(task);
            } else {
                for (String dependecyId : taskDependencies) {
                    Task dependencyTask = taskById.get(dependecyId);

                    if (dependencyTask == null || dependencyTask.getStatus() != TaskStatus.COMPLETED) {
                        eligible = false;
                        break;
                    }
                }
                if (eligible) {
                    eligibleTasks.add(task);
                }
            }
        }

        // Orders the tasks by priority
        eligibleTasks.sort(Comparator.comparingInt(Task::getPriority));

        return eligibleTasks;
    }

    /**
     * This method has a time complexity of O(n^3) and a space complexity of O(n).
     */
    @Override
    public List<Task> getExecutionOrder(Collection<Task> tasks) {
        List<Task> executionOrder = new ArrayList<>();

        /* 
         * To optimize time complexity and avoid O(n), all tasks are mapped by their ID.
         * Thus, any dependency lookup is done in O(1).
         * 
         * Furthermore, to avoid altering the original state of the tasks, a new list of tasks is created with a deep copy.
         */
        Map<String, Task> taskById = new HashMap<>();
        List<Task> deepCoppiedTasks = new ArrayList<>();
        for (Task task : tasks) {
            taskById.put(task.getId(), task);
            Task deepCoppiedTask = new Task(task);
            deepCoppiedTasks.add(deepCoppiedTask);
        } 

        // Detect missing dependencies
        for (Task task : tasks) {
            for (String dependencyID : task.getDependencies()) {
                if (!taskById.containsKey(dependencyID)) {
                    throw new IllegalArgumentException("The dependency " + dependencyID + " doesn't exist.");
                }
            }
        }

        while (executionOrder.size() != tasks.size()) {
            List<Task> eligibleTasks = this.getEligibleTasks(deepCoppiedTasks);

            if (eligibleTasks.isEmpty()) {
                throw new IllegalArgumentException("Circular dependency detected.");
            }

            Task highestPriorityTask = eligibleTasks.get(0);

            executionOrder.add(taskById.get(highestPriorityTask.getId()));

            highestPriorityTask.setStatus(TaskStatus.COMPLETED);
        }
        return executionOrder;
    }
}
