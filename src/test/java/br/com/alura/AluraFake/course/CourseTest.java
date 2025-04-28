package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseTest {

    private Course course;

    @BeforeEach
    void setUp() {
        User instructorMock = mock(User.class);
        when(instructorMock.isInstructor()).thenReturn(true);
        course = new Course("Java OO", "Estude OO com Java", instructorMock);
    }

    @Test
    void hasAnyTaskWithSameStatement_should_return_true_when_statement_is_the_same_in_a_persisted_task() {
        final String statement = "Task 1";
        course.addOpenTextTask(statement, 1);
        assertTrue(course.hasAnyTaskWithSameStatement(statement));
    }

    @Test
    void hasAnyTaskWithSameStatement_should_return_false_when_statement_is_not_the_same_in_persisted_tasks() {
        course.addOpenTextTask("Task 1", 1);
        assertFalse(course.hasAnyTaskWithSameStatement("Other task"));
    }


    @Test
    void addOpenTextTask_should_throw_exception_when_inserting_task_with_order_leaping_the_last_persisted_order() {
        course.addOpenTextTask("Task 1", 1);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addOpenTextTask("Task 2", 3);
        });
        assertEquals("The order has to be in an insertable position.", exception.getMessage());
    }

    @Test
    void addOpenTextTask_should_insert_first_task_when_list_is_empty() {
        course.addOpenTextTask("Task 1", 1);
        assertEquals(1, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
    }

    @Test
    void addOpenTextTask_should_insert_task_in_the_end_whithout_shifting_any_task() {
        course.addOpenTextTask("Task 1", 1);
        course.addOpenTextTask("Task 2", 2);

        assertEquals(2, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("Task 1", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("Task 2", course.getTasks().get(1).getStatement());
    }

    @Test
    void addOpenTextTask_should_shift_existing_tasks_when_inserting_task_in_middle() {
        course.addOpenTextTask("Task 1", 1);
        course.addOpenTextTask("Task 2", 2);
        course.addOpenTextTask("New Task", 2);

        assertEquals(3, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("Task 1", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("New Task", course.getTasks().get(1).getStatement());
        assertEquals(3, course.getTasks().get(2).getOrder());
        assertEquals("Task 2", course.getTasks().get(2).getStatement());
    }

    @Test
    void addOpenTextTask_should_shift_all_tasks_when_inserting_task_in_the_begining() {
        course.addOpenTextTask("Task 1", 1);
        course.addOpenTextTask("Task 2", 2);
        course.addOpenTextTask("New Task", 1);

        assertEquals(3, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("New Task", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("Task 1", course.getTasks().get(1).getStatement());
        assertEquals(3, course.getTasks().get(2).getOrder());
        assertEquals("Task 2", course.getTasks().get(2).getStatement());
    }
}