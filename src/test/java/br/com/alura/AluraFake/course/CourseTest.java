package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Option;
import br.com.alura.AluraFake.task.SingleChoiceTask;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void addOpenTextTask_should_throw_exception_when_trying_to_insert_task_with_order_higher_than_one_when_list_is_empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addOpenTextTask("Task", 2);
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

    @Test
    void addSingleChoiceTask_should_throw_exception_when_inserting_task_with_order_leaping_the_last_persisted_order() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfOptions());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addSingleChoiceTask("Task 2", 3, getDefaultListOfOptions());
        });
        assertEquals("The order has to be in an insertable position.", exception.getMessage());
    }

    @Test
    void addSingleChoiceTask_should_throw_exception_when_trying_to_insert_task_with_order_higher_than_one_when_list_is_empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addSingleChoiceTask("Task", 2, getDefaultListOfOptions());
        });
        assertEquals("The order has to be in an insertable position.", exception.getMessage());
    }

    @Test
    void addSingleChoiceTask_should_insert_first_task_when_list_is_empty() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfOptions());
        assertEquals(1, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
    }

    @Test
    void addSingleChoiceTask_should_insert_task_in_the_end_whithout_shifting_any_task() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfOptions());
        course.addSingleChoiceTask("Task 2", 2, getDefaultListOfOptions());

        assertEquals(2, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("Task 1", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("Task 2", course.getTasks().get(1).getStatement());
    }

    @Test
    void addSingleChoiceTask_should_shift_existing_tasks_when_inserting_task_in_middle() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfOptions());
        course.addSingleChoiceTask("Task 2", 2, getDefaultListOfOptions());
        course.addSingleChoiceTask("New Task", 2, getDefaultListOfOptions());

        assertEquals(3, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("Task 1", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("New Task", course.getTasks().get(1).getStatement());
        assertEquals(3, course.getTasks().get(2).getOrder());
        assertEquals("Task 2", course.getTasks().get(2).getStatement());
    }

    @Test
    void addSingleChoiceTask_should_shift_all_tasks_when_inserting_task_in_the_begining() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfOptions());
        course.addSingleChoiceTask("Task 2", 2, getDefaultListOfOptions());
        course.addSingleChoiceTask("New Task", 1, getDefaultListOfOptions());

        assertEquals(3, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("New Task", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("Task 1", course.getTasks().get(1).getStatement());
        assertEquals(3, course.getTasks().get(2).getOrder());
        assertEquals("Task 2", course.getTasks().get(2).getStatement());
    }

    @Test
    void addSingleChoiceTask_should_throw_exception_when_there_is_more_than_one_correct_option() {
        List<Option> options = List.of(
                new Option("Java", true),
                new Option("Python", true),
                new Option("Ruby", false)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addSingleChoiceTask("Task with multiple correct options", 1, options);
        });

        assertEquals("There must be exactly one correct option.", exception.getMessage());
    }

    @Test
    void addSingleChoiceTask_should_throw_exception_when_there_are_duplicated_options() {
        List<Option> options = List.of(
                new Option("Java", true),
                new Option("Java", false)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addSingleChoiceTask("Task with duplicated options", 1, options);
        });

        assertEquals("Options must be unique.", exception.getMessage());
    }

    @Test
    void newSingleChoiceExercise__should_return_bad_request_when_option_is_equal_to_statement() {
        String statement = "Qual linguagem é tipada estaticamente?";
        List<Option> options = List.of(
                new Option("Qual linguagem é tipada estaticamente?", true),
                new Option("Python", false),
                new Option("JavaScript", false)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addSingleChoiceTask(statement, 1, options);
        });

        assertEquals("Options must be different from the statement.", exception.getMessage());
    }

    @Test
    void newSingleChoiceExercise__should_add_task_correctly_with_two_options() {
        String statement = "Qual palavra-chave define uma constante em Java?";
        List<Option> options = List.of(
                new Option("final", true),
                new Option("const", false)
        );

        course.addSingleChoiceTask(statement, 1, options);

        assertEquals(1, course.getTasks().size());
        Task addedTask = course.getTasks().getFirst();
        assertInstanceOf(SingleChoiceTask.class, addedTask);
        SingleChoiceTask singleChoiceTask = (SingleChoiceTask) addedTask;
        assertEquals(statement, singleChoiceTask.getStatement());
        assertEquals(2, singleChoiceTask.getOptions().size());
        assertEquals(1, singleChoiceTask.getOptions().stream().filter(Option::isCorrect).count());
    }

    @Test
    void newSingleChoiceExercise__should_add_task_correctly_with_five_options() {
        String statement = "Qual destas é uma interface funcional em Java?";
        List<Option> options = List.of(
                new Option("Runnable", true),
                new Option("List", false),
                new Option("HashMap", false),
                new Option("Thread", false),
                new Option("ArrayList", false)
        );

        course.addSingleChoiceTask(statement, 1, options);

        assertEquals(1, course.getTasks().size());
        Task addedTask = course.getTasks().getFirst();
        assertInstanceOf(SingleChoiceTask.class, addedTask);
        SingleChoiceTask singleChoiceTask = (SingleChoiceTask) addedTask;
        assertEquals(statement, singleChoiceTask.getStatement());
        assertEquals(5, singleChoiceTask.getOptions().size());
        assertEquals(1, singleChoiceTask.getOptions().stream().filter(Option::isCorrect).count());
    }

    private List<Option> getDefaultListOfOptions() {
        return List.of(
                new Option("Answer 1", true),
                new Option("Answer 2", false),
                new Option("Answer 3", false)
        );
    }
}