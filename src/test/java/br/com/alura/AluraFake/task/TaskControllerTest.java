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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(status().isBadRequest());

//                .andExpect(jsonPath("$[0].field").value("statement"))
//                .andExpect(jsonPath("$[0].message").isNotEmpty());
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
    void newOpenTextExercise__should_return_bad_request_when_statement_is_duplicated_in_same_course() throws Exception {

        final Long courseId = 42L;
        final String duplicatedStatement = "Statement duplicado.";

        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(courseId, duplicatedStatement, 1);
        OpenTextTask duplicatedOpenTextTaskMock = mock(OpenTextTask.class);
        Course courseMock = mock(Course.class);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(taskRepository.findByCourseIdAndStatement(courseId, duplicatedStatement))
                .thenReturn(Optional.of(duplicatedOpenTextTaskMock));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("$[0].field").value("statement"))
//                .andExpect(jsonPath("$[0].message").isNotEmpty());
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
    void newOpenTextExercise__should_return_created_when_request_is_valid() throws Exception {

        Course courseMock = mock(Course.class);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(taskRepository.findByCourseIdAndStatement(anyLong(), anyString())).thenReturn(Optional.empty());

        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "statement", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isCreated());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}



//Resumo organizado para implementação
//
//Cenário                                   Expectativa             Observação
//Atividade criada corretamente	            201 Created
//Enunciado menor que 4 caracteres          400 Bad Request         jsonPath para campo "statement"
//Enunciado maior que 255 caracteres        400 Bad Request         jsonPath para campo "statement"
//Enunciado já existente no curso           400 Bad Request         jsonPath para campo "statement"
//Ordem igual a 0 ou negativa               400 Bad Request         jsonPath para campo "order"
//Curso não encontrado                      400 Bad Request         jsonPath para campo "courseId"
//Curso com status diferente de BUILDING    400 Bad Request         jsonPath para campo "courseId"


//newOpenTextExercise__should_return_not_found_when_course_does_not_exist                       ok
//newOpenTextExercise__should_return_bad_request_when_course_status_is_not_building             ok
//newOpenTextExercise__should_return_bad_request_when_statement_is_too_short                    ok
//newOpenTextExercise__should_return_bad_request_when_statement_is_too_long                     ok
//newOpenTextExercise__should_return_bad_request_when_statement_is_duplicated_in_same_course    ok
//newOpenTextExercise__should_return_bad_request_when_order_is_not_positive                     ok
//newOpenTextExercise__should_return_created_when_request_is_valid