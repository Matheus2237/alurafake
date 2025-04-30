package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static br.com.alura.AluraFake.course.Status.BUILDING;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class TaskController {

    private final CourseRepository courseRepository;

    @Autowired
    public TaskController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostMapping("/task/new/opentext")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity newOpenTextExercise(@Valid @RequestBody OpenTextTaskDTO openTextTaskDTO) {
        Course course = getCourseIfPersistedByItsId(openTextTaskDTO.courseId());
        validateTaskForCourse(course, openTextTaskDTO.statement());
        course.addOpenTextTask(openTextTaskDTO.statement(), openTextTaskDTO.order());
        courseRepository.save(course);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/task/new/singlechoice")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity newSingleChoiceExercise(@Valid @RequestBody SingleChoiceTaskDTO singleChoiceTaskDTO) {
        Course course = getCourseIfPersistedByItsId(singleChoiceTaskDTO.courseId());
        validateTaskForCourse(course, singleChoiceTaskDTO.statement());
        course.addSingleChoiceTask(singleChoiceTaskDTO.statement(), singleChoiceTaskDTO.order(), singleChoiceTaskDTO.optionsAsEntites());
        courseRepository.save(course);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity newMultipleChoiceExercise(@Valid @RequestBody MultipleChoiceTaskDTO multipleChoiceTaskDTO) {
        Course course = getCourseIfPersistedByItsId(multipleChoiceTaskDTO.courseId());
        validateTaskForCourse(course, multipleChoiceTaskDTO.statement());
        course.addMultipleChoiceTask(multipleChoiceTaskDTO.statement(), multipleChoiceTaskDTO.order(), multipleChoiceTaskDTO.optionsAsEntites());
        courseRepository.save(course);
        return ResponseEntity.status(CREATED).build();
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