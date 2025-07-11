package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse, Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        Optional<User> possibleAuthor = userRepository.findById(userId).filter(User::isInstructor);

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
    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_INSTRUCTOR')")
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
