package com.premiumminds.internship.taskscheduler;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TaskSchedulerService implements ITaskSchedulerService {

    /**
     * Este método tem complexidade temporal de O(n^2) e espacial de O(n).
     */
    @Override
    public List<Task> getEligibleTasks(Collection<Task> tasks) {
        List<Task> eligibleTasks = new ArrayList<>();

        /** 
         * Para otimizar a complexidade temporal e evitar O(n), todas as tarefas estão mapeadas pelo seu ID. 
         * Assim, qualquer pesquisa por dependências é feita em O(1).
         */
        Map<String, Task> taskById = new HashMap<>();
        for (Task task : tasks) {
            taskById.put(task.getId(), task);
        }            
 
        for (Task task : tasks) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                continue;
            }

            boolean eligible = true; // Até ser provado o contrário a task é elegível

            Set<String> taskDependencies = task.getDependencies();

            // Se a task não tiver dependências então é elegível
            if (taskDependencies.isEmpty()) { 
                eligibleTasks.add(task);
            } else {
                for (String dependecyId : taskDependencies) {
                    Task dependencyTask = taskById.get(dependecyId);

                    // Se uma dependência não existir, a task é inválida
                    if (dependencyTask == null) { 
                        throw new IllegalArgumentException("A dependência " + dependecyId + " não existe!");
                    }

                    // Se uma dependência não estiver completada, então a task é automaticamente elegível
                    if (dependencyTask.getStatus() != TaskStatus.COMPLETED) {
                        eligible = false;
                        break;
                    }
                }
                if (eligible) {
                    eligibleTasks.add(task);
                }
            }
        }
        return eligibleTasks;
    }

    /**
     * Este método tem complexidade temporal de O(n^3) e espacial de O(n).
     */
    @Override
    public List<Task> getExecutionOrder(Collection<Task> tasks) {
        List<Task> executionOrder = new ArrayList<>();

        /** 
         * Para otimizar a complexidade temporal e evitar O(n), todas as tarefas estão mapeadas pelo seu ID. 
         * Assim, qualquer pesquisa por dependências é feita em O(1).
         * 
         * Para além disso, para não alterar o estado original das tasks, é criada uma nova lista de tasks com deep copy.
         */
        Map<String, Task> taskById = new HashMap<>();
        List<Task> deepCoppiedTasks = new ArrayList<>();
        for (Task task : tasks) {
            taskById.put(task.getId(), task);
            Task deepCoppiedTask = new Task(task);
            deepCoppiedTasks.add(deepCoppiedTask);
        } 


        while (executionOrder.size() != tasks.size()) {
            List<Task> eligibleTasks = this.getEligibleTasks(deepCoppiedTasks);

            if (eligibleTasks.isEmpty()) {
                throw new IllegalArgumentException("Circular dependency detected.");
            }

            Task highestPriorityTask = this.getHighestPriorityTask(eligibleTasks);

            executionOrder.add(taskById.get(highestPriorityTask.getId()));

            highestPriorityTask.setStatus(TaskStatus.COMPLETED);
        }
        return executionOrder;
    }

    /**
     * Este método tem complexidade temporal de O(n) e espacial de O(1). 
     */
    private Task getHighestPriorityTask(List<Task> tasks) {
        Task highestPriorityTask = tasks.get(0);;

        for (Task task : tasks) {
            if (task.getPriority() < highestPriorityTask.getPriority()) {
                highestPriorityTask = task;
            }
        }

        return highestPriorityTask;
    }
}
