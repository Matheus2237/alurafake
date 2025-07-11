package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.security.SecurityConfig;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
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
    @WithMockUser(username = "1", authorities = "SCOPE_INSTRUCTOR")
    void should_return_bad_request_when_user_not_found_by_id() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java");
        dto.setDescription("Curso de Java");

        doReturn(Optional.empty()).when(userRepository).findById(1L);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "1", authorities = "SCOPE_INSTRUCTOR")
    void should_return_bad_request_when_user_is_not_instructor() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java");
        dto.setDescription("Curso de Java");

        User user = mock(User.class);
        doReturn(false).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository).findById(1L);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("emailInstructor"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "1", authorities = "SCOPE_INSTRUCTOR")
    void should_return_created_when_user_is_valid_instructor() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setTitle("Java");
        dto.setDescription("Curso de Java");

        User user = mock(User.class);
        doReturn(true).when(user).isInstructor();

        doReturn(Optional.of(user)).when(userRepository).findById(1L);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void publishCourse__should_return_not_found_when_course_does_not_exist() throws Exception {
        doReturn(Optional.empty()).when(courseRepository).findById(42L);

        mockMvc.perform(post("/course/42/publish"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void publishCourse__should_return_bad_request_when_course_status_is_not_building() throws Exception {
        Course courseMock = mock(Course.class);
        doReturn(Optional.of(courseMock)).when(courseRepository).findById(42L);
        doReturn(true).when(courseMock).isPublished();

        mockMvc.perform(post("/course/42/publish"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Course must be in BUILDING status to be published."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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