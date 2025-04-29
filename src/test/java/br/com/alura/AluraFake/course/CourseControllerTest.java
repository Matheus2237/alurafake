package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.*;
import br.com.alura.AluraFake.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CourseRepository courseRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_invalid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        doReturn(Optional.empty()).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }


    @Test
    void newCourseDTO__should_return_bad_request_when_email_is_no_instructor() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(false).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository)
                .findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void newCourseDTO__should_return_created_when_new_course_request_is_valid() throws Exception {

        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setTitle("Java");
        newCourseDTO.setDescription("Curso de Java");
        newCourseDTO.setEmailInstructor("paulo@alura.com.br");

        User user = mock(User.class);
        doReturn(true).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(newCourseDTO.getEmailInstructor());

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void listAllCourses__should_list_all_courses() throws Exception {
        User paulo = new User("Paulo", "paulo@alua.com.br", Role.INSTRUCTOR);

        Course java = new Course("Java", "Curso de java", paulo);
        Course hibernate = new Course("Hibernate", "Curso de hibernate", paulo);
        Course spring = new Course("Spring", "Curso de spring", paulo);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(java, hibernate, spring));

        mockMvc.perform(get("/course/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description").value("Curso de java"))
                .andExpect(jsonPath("$[1].title").value("Hibernate"))
                .andExpect(jsonPath("$[1].description").value("Curso de hibernate"))
                .andExpect(jsonPath("$[2].title").value("Spring"))
                .andExpect(jsonPath("$[2].description").value("Curso de spring"));
    }

    @Test
    void publishCourse__should_return_not_found_when_course_does_not_exist() throws Exception {
        doReturn(Optional.empty()).when(courseRepository).findById(42L);

        mockMvc.perform(post("/course/42/publish"))
                .andExpect(status().isNotFound());
    }

    @Test
    void publishCourse__should_return_bad_request_when_course_status_is_not_building() throws Exception {
        Course courseMock = mock(Course.class);
        doReturn(Optional.of(courseMock)).when(courseRepository).findById(42L);
        doReturn(true).when(courseMock).isPublished();

        mockMvc.perform(post("/course/42/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Course must be in BUILDING status to be published."));
    }

    @Test
    void publishCourse__should_return_bad_request_when_course_has_missing_task_types() throws Exception {
        Course courseMock = mock(Course.class);
        doReturn(Optional.of(courseMock)).when(courseRepository).findById(42L);
        doReturn(false).when(courseMock).isPublished();
        doReturn(false).when(courseMock).hasAllTypeOfTasks();

        mockMvc.perform(post("/course/42/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Course must have at least one task of each type."));
    }

    @Test
    void publishCourse__should_return_bad_request_when_course_has_non_continuous_order() throws Exception {
        Course courseMock = mock(Course.class);
        doReturn(Optional.of(courseMock)).when(courseRepository).findById(42L);
        doReturn(false).when(courseMock).isPublished();
        doReturn(true).when(courseMock).hasAllTypeOfTasks();
        doReturn(false).when(courseMock).hasAllTasksInValidOrder();

        mockMvc.perform(post("/course/42/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Course must have all tasks in continuous order."));
    }

    @Test
    void publishCourse__should_publish_course_when_valid() throws Exception {
        Course courseMock = mock(Course.class);
        doReturn(Optional.of(courseMock)).when(courseRepository).findById(42L);
        doReturn(false).when(courseMock).isPublished();
        doReturn(true).when(courseMock).hasAllTypeOfTasks();
        doReturn(true).when(courseMock).hasAllTasksInValidOrder();

        mockMvc.perform(post("/course/42/publish"))
                .andExpect(status().isOk());

        verify(courseMock, times(1)).publish();
        verify(courseRepository, times(1)).save(any(Course.class));
    }
}