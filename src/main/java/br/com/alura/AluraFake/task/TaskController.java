package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static br.com.alura.AluraFake.course.Status.BUILDING;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class TaskController {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(CourseRepository courseRepository, TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @PostMapping("/task/new/opentext")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody OpenTextTaskDTO openTextTaskDTO) {
        Course course = getCourseIfPersistedByItsId(openTextTaskDTO.courseId());
        validateTaskForCourse(course, openTextTaskDTO.statement());
        course.addOpenTextTask(openTextTaskDTO.statement(), openTextTaskDTO.order());
        courseRepository.save(course);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    public ResponseEntity newSingleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

    private Course getCourseIfPersistedByItsId(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course doesn't exist"));
    }

    private void validateTaskForCourse(Course course, String statement) {
        if (!BUILDING.equals(course.getStatus())) {
            throw new IllegalStateException("Course has to be in building phase to allow tasks registrations.");
        }
        if (course.getTitle().equals(statement)) {
            throw new IllegalArgumentException("The task's statement is the same as the course title.");
        }
        if (course.hasAnyTaskWithSameStatement(statement)) {
            throw new EntityExistsException("A task with the same statement already exists for this course.");
        }
    }
}