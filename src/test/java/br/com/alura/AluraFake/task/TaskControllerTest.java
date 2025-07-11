package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static br.com.alura.AluraFake.course.Status.BUILDING;
import static br.com.alura.AluraFake.course.Status.PUBLISHED;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newOpenTextExercise__should_return_bad_request_when_courseId_is_null() throws Exception {
        OpenTextTaskDTO dto = new OpenTextTaskDTO(null, "statement", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newOpenTextExercise__should_return_not_found_when_course_does_not_exist() throws Exception {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "statement", 1);
        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
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
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newOpenTextExercise__should_create_task_normally_when_all_data_is_valid_and_there_is_no_problem_in_order() throws Exception {

        Course courseMock = mock(Course.class);
        OpenTextTaskDTO newOpenTextTaskDTO = new OpenTextTaskDTO(42L, "New Task", 1);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        doNothing().when(courseMock).addOpenTextTask(anyString(), anyInt());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOpenTextTaskDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_courseId_is_null() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(null, "statement", 1, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_not_found_when_course_does_not_exist() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(42L, "statement", 1, optionsDTO);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_course_status_is_not_building() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(42L, "statement", 1, optionsDTO);
        Course courseMock = mock(Course.class);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(PUBLISHED);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Course has to be in building phase to allow tasks registrations."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_statement_is_null_or_blank() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO nullStatementDto = new SingleChoiceTaskDTO(42L, null, 1, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());

        SingleChoiceTaskDTO emptyStatementDto = new SingleChoiceTaskDTO(42L, "", 1, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_statement_has_less_than_4_characters() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(42L, "abc", 1, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_statement_has_more_than_255_characters() throws Exception {
        final String longStatement = "Questão inválida porque o enunciado fornecido excede o limite máximo de caracteres permitidos, o que resulta em falha ao processar a questão para avaliação. Por favor, reduzir o comprimento do texto para garantir que ele seja aceito pelo sistema sem erros.";
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(42L, longStatement, 1, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_task_statement_is_duplicated_with_course_title() throws Exception {
        final Long courseId = 42L;
        final String statement = "Statement";
        Course courseMock = mock(Course.class);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn(statement);

        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(courseId, statement, 1, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The task's statement is the same as the course title."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_statement_is_duplicated_in_same_course() throws Exception {
        final Long courseId = 42L;
        final String duplicatedStatement = "Statement duplicado.";
        Course courseMock = mock(Course.class);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(duplicatedStatement)).thenReturn(true);

        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(courseId, duplicatedStatement, 1, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A task with the same statement already exists for this course."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_order_is_null() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(42L, "statement", null, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_order_is_not_positive() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO dto = new SingleChoiceTaskDTO(42L, "statement", 0, optionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_order_skips_sequence() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 3, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("The order has to be in a insertable position."))
                .when(courseMock).addSingleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The order has to be in a insertable position."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_options_are_null_or_empty() throws Exception {
        SingleChoiceTaskDTO nullStatementDto = new SingleChoiceTaskDTO(42L, "Statement", 1, null);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());

        SingleChoiceTaskDTO emptyStatementDto = new SingleChoiceTaskDTO(42L, "Statement", 1, List.of());
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_options_are_less_than_two() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_options_are_more_than_five() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false),
                new OptionDTO("Haskell", false),
                new OptionDTO("JavaScript", false),
                new OptionDTO("Python", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_option_text_is_null_or_blank() throws Exception {
        List<OptionDTO> nullTextOptionsDTO = List.of(
                new OptionDTO(null, true),
                new OptionDTO("Java", false)
        );
        SingleChoiceTaskDTO nullStatementDto = new SingleChoiceTaskDTO(42L, "Statement", 1, nullTextOptionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[0].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());

        List<OptionDTO> emptyTextOptionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("", false)
        );
        SingleChoiceTaskDTO emptyStatementDto = new SingleChoiceTaskDTO(42L, "Statement", 1, emptyTextOptionsDTO);
        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[1].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_option_text_is_too_short() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Jav", true),
                new OptionDTO("Python", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[0].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_option_text_is_too_long() throws Exception {
        Course courseMock = mock(Course.class);
        String longOption = "A".repeat(81);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO(longOption, true),
                new OptionDTO("Python", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("options[0].option"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_there_is_more_than_one_correct_option() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", true),
                new OptionDTO("Ruby", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("There must be exactly one correct option."))
                .when(courseMock).addSingleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("There must be exactly one correct option."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_options_are_duplicated() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Java", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("Options must be unique."))
                .when(courseMock).addSingleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Options must be unique."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_return_bad_request_when_option_is_equal_to_statement() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Statement", true),
                new OptionDTO("Python", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("Options must be different from the statement."))
                .when(courseMock).addSingleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Options must be different from the statement."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newSingleChoiceExercise__should_create_task_normally_when_all_data_is_valid_and_there_is_no_problem_in_order() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false),
                new OptionDTO("Haskell", false),
                new OptionDTO("Javascript", false)
        );
        SingleChoiceTaskDTO newSingleTextTaskDTO = new SingleChoiceTaskDTO(42L, "New Task", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doNothing().when(courseMock).addSingleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSingleTextTaskDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_courseId_is_null() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(null, "statement", 1, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("courseId"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_not_found_when_course_does_not_exist() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(42L, "statement", 1, optionsDTO);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_course_status_is_not_building() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(42L, "statement", 1, optionsDTO);
        Course courseMock = mock(Course.class);
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(PUBLISHED);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Course has to be in building phase to allow tasks registrations."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_statement_is_null_or_blank() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO nullStatementDto = new MultipleChoiceTaskDTO(42L, null, 1, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());

        MultipleChoiceTaskDTO emptyStatementDto = new MultipleChoiceTaskDTO(42L, "", 1, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyStatementDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_statement_has_less_than_4_characters() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(42L, "abc", 1, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_statement_has_more_than_255_characters() throws Exception {
        final String longStatement = "Questão inválida porque o enunciado fornecido excede o limite máximo de caracteres permitidos, o que resulta em falha ao processar a questão para avaliação. Por favor, reduzir o comprimento do texto para garantir que ele seja aceito pelo sistema sem erros.";
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(42L, longStatement, 1, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_task_statement_is_duplicated_with_course_title() throws Exception {
        final Long courseId = 42L;
        final String statement = "Statement";
        Course courseMock = mock(Course.class);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn(statement);

        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(courseId, statement, 1, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The task's statement is the same as the course title."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_statement_is_duplicated_in_same_course() throws Exception {
        final Long courseId = 42L;
        final String duplicatedStatement = "Statement duplicado.";
        Course courseMock = mock(Course.class);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(duplicatedStatement)).thenReturn(true);

        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(courseId, duplicatedStatement, 1, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("A task with the same statement already exists for this course."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_order_is_null() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(42L, "statement", null, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_order_is_not_positive() throws Exception {
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO dto = new MultipleChoiceTaskDTO(42L, "statement", 0, optionsDTO);
        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_there_are_no_correct_options() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", false),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new MultipleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("There must be at least two correct options and one incorrect option."))
                .when(courseMock).addMultipleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("There must be at least two correct options and one incorrect option."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_there_are_not_enough_correct_options() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Python", false),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new MultipleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("There must be at least two correct options and one incorrect option."))
                .when(courseMock).addMultipleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("There must be at least two correct options and one incorrect option."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_all_options_are_correct() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Hibernate", true)
        );
        MultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new MultipleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("There must be at least two correct options and one incorrect option."))
                .when(courseMock).addMultipleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("There must be at least two correct options and one incorrect option."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_options_are_duplicated() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Java", false),
                new OptionDTO("Spring", true)
        );
        MultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new MultipleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("Options must be unique."))
                .when(courseMock).addMultipleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Options must be unique."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_return_bad_request_when_option_is_equal_to_statement() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Statement", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false)
        );
        MultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new MultipleChoiceTaskDTO(42L, "Statement", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doThrow(new IllegalArgumentException("Options must be different from the statement."))
                .when(courseMock).addMultipleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Options must be different from the statement."));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_INSTRUCTOR")
    void newMultipleChoiceExercise__should_create_task_normally_when_all_data_is_valid() throws Exception {
        Course courseMock = mock(Course.class);
        List<OptionDTO> optionsDTO = List.of(
                new OptionDTO("Java", true),
                new OptionDTO("Spring", true),
                new OptionDTO("Ruby", false),
                new OptionDTO("Python", false)
        );
        MultipleChoiceTaskDTO newMultipleChoiceTaskDTO = new MultipleChoiceTaskDTO(42L, "New Task", 1, optionsDTO);

        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(courseMock));
        when(courseMock.getStatus()).thenReturn(BUILDING);
        when(courseMock.getTitle()).thenReturn("Title");
        when(courseMock.hasAnyTaskWithSameStatement(anyString())).thenReturn(false);
        doNothing().when(courseMock).addMultipleChoiceTask(anyString(), anyInt(), any());

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMultipleChoiceTaskDTO)))
                .andExpect(status().isCreated());

        verify(courseRepository, times(1)).save(any(Course.class));
    }
}