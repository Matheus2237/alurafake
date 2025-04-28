package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String title;
    private String description;
    @ManyToOne
    private User instructor;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("task_order ASC")
    private List<Task> tasks;

    @Deprecated
    public Course(){}

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "Usuario deve ser um instrutor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
        tasks = new ArrayList<>();
    }

    public boolean hasAnyTaskWithSameStatement(String statement) {
        Stream<Task> stream = tasks.stream();
        return stream.anyMatch(task -> task.getStatement().equals(statement));
    }

    public void addOpenTextTask(String statement, Integer order) {
        Assert.isTrue(isOrderValidToInsert(order), "The order has to be in an insertable position.");
        Task task = new OpenTextTask(statement, order, this);
        insertNewTaskShiftingSubsequentTasks(task);
    }

    private boolean isOrderValidToInsert(Integer order) {
        return tasks.isEmpty() || tasks.stream().map(Task::getOrder).anyMatch(o -> o.equals(order))
                || tasks.getLast().getOrder().equals(order-1);
    }

    private void insertNewTaskShiftingSubsequentTasks(Task task) {
        Collections.sort(tasks);
        int insertPosition = task.getOrder() - 1;
        tasks.add(insertPosition, task);
        for (int i = insertPosition + 1; i < tasks.size(); i++) {
            tasks.get(i).incrementOrder();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
