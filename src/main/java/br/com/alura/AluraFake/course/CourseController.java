package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.*;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, UserRepository userRepository){
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {

        //Caso implemente o bonus, pegue o instrutor logado
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);

        if(possibleAuthor.isEmpty()) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new ErrorItemDTO("emailInstructor", "Usuário não é um instrutor"));
        }

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());

        courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/course/all")
    public ResponseEntity<List<CourseListItemDTO>> createCourse() {
        List<CourseListItemDTO> courses = courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/course/{id}/publish")
    public ResponseEntity createCourse(@PathVariable("id") Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course doesn't exist"));

        if (course.isPublished()) {
            return ResponseEntity.status(BAD_REQUEST).body("Course must be in BUILDING status to be published.");
        }

        if (!course.hasAllTypeOfTasks()) {
            return ResponseEntity.status(BAD_REQUEST).body("Course must have at least one task of each type.");
        }

        if (!course.hasAllTasksInValidOrder()) {
            return ResponseEntity.status(BAD_REQUEST).body("Course must have all tasks in continuous order.");
        }

        course.publish();
        courseRepository.save(course);

        return ResponseEntity.ok().build();
    }

}
