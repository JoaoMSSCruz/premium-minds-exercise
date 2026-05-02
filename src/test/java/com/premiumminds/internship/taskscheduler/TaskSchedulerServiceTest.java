package com.premiumminds.internship.taskscheduler;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskSchedulerServiceTest {

    private ITaskSchedulerService service;

    @BeforeEach
    void setUp() {
        service = new TaskSchedulerService();
    }

    @Test
    void testSingleTaskNoDependencies() {

        Task a = new Task("A", 1, Set.of());
        List<Task> order = service.getExecutionOrder(Set.of(a));

        assertEquals(1, order.size());
        assertEquals("A", order.get(0).getId());
    }

    @Test
    void testLinearDependency() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 1, Set.of("A"));

        List<Task> order = service.getExecutionOrder(Set.of(b, a));

        assertEquals(2, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("B", order.get(1).getId());
    }

    @Test
    void testPriorityBreaksTie() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 2, Set.of("A"));
        Task c = new Task("C", 1, Set.of("A"));

        List<Task> order = service.getExecutionOrder(Set.of(a, b, c));

        assertEquals(3, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("C", order.get(1).getId());
        assertEquals("B", order.get(2).getId());
    }

    @Test
    void testCircularDependencyThrows() {
        Task a = new Task("A", 1, Set.of("B"));
        Task b = new Task("B", 1, Set.of("A"));

        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a, b)));
    }

    @Test
    void testMissingDependencyThrows() {
        Task a = new Task("A", 1, Set.of("X"));

        assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionOrder(Set.of(a)));
    }

    @Test
    void testEligibleTasksNoDependencies() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 1, Set.of());

        List<Task> eligible = service.getEligibleTasks(Set.of(a, b));

        assertEquals(2, eligible.size());
    }

    @Test
    void testEligibleTasksWithPendingDependency() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 1, Set.of("A"));

        List<Task> eligible = service.getEligibleTasks(Set.of(a, b));

        assertEquals(1, eligible.size());
        assertEquals("A", eligible.get(0).getId());
    }

    @Test
    void testMultipleDependenciesDiamond() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 2, Set.of("A"));
        Task c = new Task("C", 2, Set.of("A"));
        Task d = new Task("D", 1, Set.of("B", "C"));

        List<Task> order = service.getExecutionOrder(Set.of(a, b, c, d));

        assertEquals(4, order.size());
        assertEquals("A", order.get(0).getId());
        assertEquals("D", order.get(3).getId());
    }

    @Test
    void testPriorityCannotOverrideDependency() {
        Task a = new Task("A", 1, Set.of("B"));
        Task b = new Task("B", 10, Set.of());

        List<Task> order = service.getExecutionOrder(Set.of(a, b));

        assertEquals(2, order.size());
        assertEquals("B", order.get(0).getId());
        assertEquals("A", order.get(1).getId());
    }

    @Test
    void testIndependentChains() {
        Task a1 = new Task("A1", 1, Set.of());
        Task a2 = new Task("A2", 1, Set.of("A1"));
        
        Task b1 = new Task("B1", 2, Set.of());
        Task b2 = new Task("B2", 2, Set.of("B1"));

        List<Task> order = service.getExecutionOrder(Set.of(a1, a2, b1, b2));

        assertEquals(4, order.size());
        assertEquals("A1", order.get(0).getId());
        assertEquals("A2", order.get(1).getId());
        assertEquals("B1", order.get(2).getId());
        assertEquals("B2", order.get(3).getId());
    }

    @Test
    void testOriginalTasksRemainUnmodified() {
        Task a = new Task("A", 1, Set.of());
        Task b = new Task("B", 1, Set.of("A"));

        service.getExecutionOrder(Set.of(a, b));

        assertEquals(TaskStatus.PENDING, a.getStatus());
        assertEquals(TaskStatus.PENDING, b.getStatus());
    }

    @Test
    void testEmptyCollection() {
        List<Task> order = service.getExecutionOrder(Set.of());
        assertTrue(order.isEmpty());
    }
}
