package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static br.com.alura.AluraFake.course.Status.BUILDING;
import static br.com.alura.AluraFake.course.Status.PUBLISHED;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    void newOpenTextExercise__should_return_bad_request_when_courseId_is_null() throws Exception {

        OpenTextTaskDTO nullOrderOpenTextTaskDTO = new OpenTextTaskDTO(null, "statement", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullOrderOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_not_found_when_course_does_not_exist() throws Exception {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "statement", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_course_status_is_not_building() throws Exception {
        Course courseMock = mock(Course.class);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(PUBLISHED);
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "statement", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Course has to be in building phase to allow tasks registrations."));
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_statement_is_null_or_blank() throws Exception {

        OpenTextTaskDTO nullStatementOpenTextTaskDTO = new OpenTextTaskDTO(42L, null, 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullStatementOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());

        OpenTextTaskDTO emptyStatementOpenTextTaskDTO = new OpenTextTaskDTO(42L, "", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyStatementOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_statement_has_less_than_4_characters() throws Exception {

        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "abc", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_statement_has_more_than_255_characters() throws Exception {

        final String longStatement = "Questão inválida porque o enunciado fornecido excede o limite máximo de caracteres permitidos, o que resulta em falha ao processar a questão para avaliação. Por favor, reduzir o comprimento do texto para garantir que ele seja aceito pelo sistema sem erros.";
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, longStatement, 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_task_statement_is_duplicated_with_course_title() throws Exception {

        final Long courseId = 42L;
        final String statement = "Statement";

        Course courseMock = mock(Course.class);
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(courseId, statement, 1);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn(statement);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The task's statement is the same as the course title."));
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_statement_is_duplicated_in_same_course() throws Exception {

        final Long courseId = 42L;
        final String duplicatedStatement = "Statement duplicado.";

        Course courseMock = mock(Course.class);
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(courseId, duplicatedStatement, 1);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(duplicatedStatement)).thenReturn(true);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A task with the same statement already exists for this course."));
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_order_is_null() throws Exception {

        OpenTextTaskDTO nullOrderOpenTextTaskDTO = new OpenTextTaskDTO(42L, "statement", null);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullOrderOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_order_is_not_positive() throws Exception {

        OpenTextTaskDTO nullOrderOpenTextTaskDTO = new OpenTextTaskDTO(42L, "statement", 0);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullOrderOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());

        OpenTextTaskDTO negativeOrderOpenTextTaskDTO = new OpenTextTaskDTO(42L, "statement", -1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeOrderOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newOpenTextExercise__should_return_bad_request_when_order_skips_sequence() throws Exception {
        Course courseMock = mock(Course.class);
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "Statement", 3);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("The order has to be in a insertable position."))
                .when(courseMock).addOpenTextTask(anyString(), anyInt());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The order has to be in a insertable position."));
    }

    @Test
    void newOpenTextExercise__should_create_task_normally_when_all_data_is_valid_and_there_is_no_problem_in_order() throws Exception {

        Course courseMock = mock(Course.class);
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "New Task", 1);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(taskRepository.findByCourseIdAndStatement(anyLong(), anyString())).thenReturn(Optional.empty());
        doNothing().when(courseMock).addOpenTextTask(anyString(), anyInt());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }
}