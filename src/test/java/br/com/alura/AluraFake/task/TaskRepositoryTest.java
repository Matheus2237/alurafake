package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static br.com.alura.AluraFake.user.Role.INSTRUCTOR;
import static org.assertj.core.api.Assertions.assertThat;


// TODO: Remove if not used in the end
//@DataJpaTest
//@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

//    @Test
    void findByCourseIdAndStatement_should_return_empty_optional_when_doenst_exist_the_specified_task() {

        final String statement = "Qual foi a linguagem estudada?";
        User instructor = createAndPopulateInstructor();
        Course course = createAndPopulateCourseWithStatementAndInstructor(statement, instructor);
        Long courseId = course.getId();

        final String otherStatement = "Quando a linguagem foi criada?";
        final Long otherCourseId = courseId + 1;

        assertThat(taskRepository.findByCourseIdAndStatement(courseId, otherStatement)).isEmpty();      // Has to fail when the id exists but the statement doesn't
        assertThat(taskRepository.findByCourseIdAndStatement(otherCourseId, statement)).isEmpty();      // Has to fail when the id doesn't exists but the statement does
        assertThat(taskRepository.findByCourseIdAndStatement(otherCourseId, otherStatement)).isEmpty(); // Has to fail when both id and statement doesn't exist
    }

//    @Test
    void findByCourseIdAndStatement_should_return_entity_when_the_specified_task_exists() {

        final String statement = "Qual foi a linguagem estudada?";
        User instructor = createAndPopulateInstructor();
        Course course = createAndPopulateCourseWithStatementAndInstructor(statement, instructor);
        Long courseId = course.getId();

        Optional<Task> task = taskRepository.findByCourseIdAndStatement(courseId, statement);
        assertThat(task).isPresent();
        assertThat(task.get().getStatement()).isEqualTo(statement);
        assertThat(task.get().getCourse().getId()).isEqualTo(courseId);
    }

    private User createAndPopulateInstructor() {
        User instructor = new User("Matheus Paulino", "matheus@alura.com.br", INSTRUCTOR);
        userRepository.save(instructor);
        return instructor;
    }

    private Course createAndPopulateCourseWithStatementAndInstructor(String statement, User instructor) {
        Course course = new Course("Java OO", "Aprenda os conceitos de OO", instructor);
        course.addOpenTextTask(statement, 1);
        courseRepository.save(course);
        return course;
    }
}