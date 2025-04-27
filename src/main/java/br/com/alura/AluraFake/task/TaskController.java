package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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

        Long courseId = openTextTaskDTO.courseId();
        String statement = openTextTaskDTO.statement();

        Optional<Course> possibleCourse = courseRepository.findById(courseId);
        if (possibleCourse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course course = possibleCourse.get();
        if (!BUILDING.equals(course.getStatus())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Task> possibleDuplicatedTask = taskRepository.findByCourseIdAndStatement(courseId, statement);
        if (possibleDuplicatedTask.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Integer order = openTextTaskDTO.order();
        OpenTextTask openTextTask = new OpenTextTask(statement, order, course);
        taskRepository.save(openTextTask);

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

}