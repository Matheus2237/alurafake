package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.MultipleChoiceTask;
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
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfSingleChoiceOptions());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addSingleChoiceTask("Task 2", 3, getDefaultListOfSingleChoiceOptions());
        });
        assertEquals("The order has to be in an insertable position.", exception.getMessage());
    }

    @Test
    void addSingleChoiceTask_should_throw_exception_when_trying_to_insert_task_with_order_higher_than_one_when_list_is_empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addSingleChoiceTask("Task", 2, getDefaultListOfSingleChoiceOptions());
        });
        assertEquals("The order has to be in an insertable position.", exception.getMessage());
    }

    @Test
    void addSingleChoiceTask_should_insert_first_task_when_list_is_empty() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfSingleChoiceOptions());
        assertEquals(1, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
    }

    @Test
    void addSingleChoiceTask_should_insert_task_in_the_end_whithout_shifting_any_task() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfSingleChoiceOptions());
        course.addSingleChoiceTask("Task 2", 2, getDefaultListOfSingleChoiceOptions());

        assertEquals(2, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("Task 1", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("Task 2", course.getTasks().get(1).getStatement());
    }

    @Test
    void addSingleChoiceTask_should_shift_existing_tasks_when_inserting_task_in_middle() {
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfSingleChoiceOptions());
        course.addSingleChoiceTask("Task 2", 2, getDefaultListOfSingleChoiceOptions());
        course.addSingleChoiceTask("New Task", 2, getDefaultListOfSingleChoiceOptions());

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
        course.addSingleChoiceTask("Task 1", 1, getDefaultListOfSingleChoiceOptions());
        course.addSingleChoiceTask("Task 2", 2, getDefaultListOfSingleChoiceOptions());
        course.addSingleChoiceTask("New Task", 1, getDefaultListOfSingleChoiceOptions());

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
    void addSingleChoiceTask__should_return_bad_request_when_option_is_equal_to_statement() {
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
    void addSingleChoiceTask__should_add_task_correctly_with_two_options() {
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
    void addSingleChoiceTask__should_add_task_correctly_with_five_options() {
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

    private List<Option> getDefaultListOfSingleChoiceOptions() {
        return List.of(
                new Option("Answer 1", true),
                new Option("Answer 2", false),
                new Option("Answer 3", false)
        );
    }

    @Test
    void addMultipleChoiceTask_should_throw_exception_when_inserting_task_with_order_leaping_the_last_persisted_order() {
        course.addMultipleChoiceTask("Task 1", 1, getDefaultListOfMultipleChoiceOptions());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addMultipleChoiceTask("Task 2", 3, getDefaultListOfMultipleChoiceOptions());
        });
        assertEquals("The order has to be in an insertable position.", exception.getMessage());
    }

    @Test
    void addMultipleChoiceTask_should_throw_exception_when_trying_to_insert_task_with_order_higher_than_one_when_list_is_empty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addMultipleChoiceTask("Task", 2, getDefaultListOfMultipleChoiceOptions());
        });
        assertEquals("The order has to be in an insertable position.", exception.getMessage());
    }

    @Test
    void addMultipleChoiceTask_should_insert_first_task_when_list_is_empty() {
        course.addMultipleChoiceTask("Task 1", 1, getDefaultListOfMultipleChoiceOptions());
        assertEquals(1, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
    }

    @Test
    void addMultipleChoiceTask_should_insert_task_in_the_end_without_shifting_any_task() {
        course.addMultipleChoiceTask("Task 1", 1, getDefaultListOfMultipleChoiceOptions());
        course.addMultipleChoiceTask("Task 2", 2, getDefaultListOfMultipleChoiceOptions());

        assertEquals(2, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("Task 1", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("Task 2", course.getTasks().get(1).getStatement());
    }

    @Test
    void addMultipleChoiceTask_should_shift_existing_tasks_when_inserting_task_in_middle() {
        course.addMultipleChoiceTask("Task 1", 1, getDefaultListOfMultipleChoiceOptions());
        course.addMultipleChoiceTask("Task 2", 2, getDefaultListOfMultipleChoiceOptions());
        course.addMultipleChoiceTask("New Task", 2, getDefaultListOfMultipleChoiceOptions());

        assertEquals(3, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("Task 1", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("New Task", course.getTasks().get(1).getStatement());
        assertEquals(3, course.getTasks().get(2).getOrder());
        assertEquals("Task 2", course.getTasks().get(2).getStatement());
    }

    @Test
    void addMultipleChoiceTask_should_shift_all_tasks_when_inserting_task_in_the_beginning() {
        course.addMultipleChoiceTask("Task 1", 1, getDefaultListOfMultipleChoiceOptions());
        course.addMultipleChoiceTask("Task 2", 2, getDefaultListOfMultipleChoiceOptions());
        course.addMultipleChoiceTask("New Task", 1, getDefaultListOfMultipleChoiceOptions());

        assertEquals(3, course.getTasks().size());
        assertEquals(1, course.getTasks().get(0).getOrder());
        assertEquals("New Task", course.getTasks().get(0).getStatement());
        assertEquals(2, course.getTasks().get(1).getOrder());
        assertEquals("Task 1", course.getTasks().get(1).getStatement());
        assertEquals(3, course.getTasks().get(2).getOrder());
        assertEquals("Task 2", course.getTasks().get(2).getStatement());
    }

    @Test
    void addMultipleChoiceTask_should_throw_exception_when_there_is_no_correct_option() {
        List<Option> options = List.of(
                new Option("Java", false),
                new Option("Spring", false),
                new Option("Kotlin", false)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addMultipleChoiceTask("Task with no incorrect options", 1, options);
        });

        assertEquals("There must be at least two correct options.", exception.getMessage());
    }

    @Test
    void addMultipleChoiceTask_should_throw_exception_when_there_are_less_than_two_correct_options() {
        List<Option> options = List.of(
                new Option("Java", true),
                new Option("Python", false),
                new Option("Ruby", false)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addMultipleChoiceTask("Task with only one correct option", 1, options);
        });

        assertEquals("There must be at least two correct options.", exception.getMessage());
    }

    @Test
    void addMultipleChoiceTask__should_fail_when_there_is_no_incorrect_option() {
        List<Option> options = List.of(
                new Option("Maven", true),
                new Option("Gradle", true),
                new Option("Word", true)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addMultipleChoiceTask("Task with no incorrect options", 1, options);
        });

        assertEquals("There must be at least one incorrect option.", exception.getMessage());
    }

    @Test
    void addMultipleChoiceTask_should_throw_exception_when_there_are_duplicated_options() {
        List<Option> options = List.of(
                new Option("Java", true),
                new Option("Java", false),
                new Option("Ruby", false)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addMultipleChoiceTask("Task with duplicated options", 1, options);
        });

        assertEquals("There must be at least two correct options.", exception.getMessage());
    }

    @Test
    void addMultipleChoiceTask_should_throw_exception_when_option_is_equal_to_statement() {
        String statement = "Qual linguagem é tipada estaticamente?";
        List<Option> options = List.of(
                new Option("Qual linguagem é tipada estaticamente?", true),
                new Option("Python", true),
                new Option("JavaScript", false)
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            course.addMultipleChoiceTask(statement, 1, options);
        });

        assertEquals("Options must be different from the statement.", exception.getMessage());
    }

    @Test
    void addMultipleChoiceTask__should_add_task_correctly_with_three_options() {
        String statement = "Quais destas são linguagens de programação?";
        List<Option> options = List.of(
                new Option("Java", true),
                new Option("Python", true),
                new Option("Photoshop", false)
        );

        course.addMultipleChoiceTask(statement, 1, options);

        assertEquals(1, course.getTasks().size());
        Task addedTask = course.getTasks().getFirst();
        assertInstanceOf(MultipleChoiceTask.class, addedTask);
        MultipleChoiceTask multipleChoiceTask = (MultipleChoiceTask) addedTask;
        assertEquals(statement, multipleChoiceTask.getStatement());
        assertEquals(3, multipleChoiceTask.getOptions().size());
        assertTrue(multipleChoiceTask.getOptions().stream().filter(Option::isCorrect).count() >= 2);
        assertTrue(multipleChoiceTask.getOptions().stream().anyMatch(option -> !option.isCorrect()));
    }

    @Test
    void addMultipleChoiceTask__should_add_task_correctly_with_five_options() {
        String statement = "Quais destes são frameworks Java?";
        List<Option> options = List.of(
                new Option("Spring", true),
                new Option("Hibernate", true),
                new Option("Vue.js", false),
                new Option("Angular", false),
                new Option("JSF", true)
        );

        course.addMultipleChoiceTask(statement, 1, options);

        assertEquals(1, course.getTasks().size());
        Task addedTask = course.getTasks().getFirst();
        assertInstanceOf(MultipleChoiceTask.class, addedTask);
        MultipleChoiceTask multipleChoiceTask = (MultipleChoiceTask) addedTask;
        assertEquals(statement, multipleChoiceTask.getStatement());
        assertEquals(5, multipleChoiceTask.getOptions().size());
        assertTrue(multipleChoiceTask.getOptions().stream().filter(Option::isCorrect).count() >= 2);
        assertTrue(multipleChoiceTask.getOptions().stream().anyMatch(option -> !option.isCorrect()));
    }

    @Test
    void addMultipleChoiceTask__should_have_at_least_one_incorrect_option() {
        String statement = "Quais são ferramentas de automação de build?";
        List<Option> options = List.of(
                new Option("Maven", true),
                new Option("Gradle", true),
                new Option("Word", false)
        );

        course.addMultipleChoiceTask(statement, 1, options);

        MultipleChoiceTask multipleChoiceTask = (MultipleChoiceTask) course.getTasks().getFirst();
        boolean hasIncorrect = multipleChoiceTask.getOptions()
                .stream()
                .anyMatch(option -> !option.isCorrect());

        assertTrue(hasIncorrect, "A multiple choice task deve ter pelo menos uma opção incorreta.");
    }

    private List<Option> getDefaultListOfMultipleChoiceOptions() {
        return List.of(
                new Option("Java", true),
                new Option("Spring", true),
                new Option("Ruby", false)
        );
    }
}