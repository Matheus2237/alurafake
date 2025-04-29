package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.*;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.alura.AluraFake.course.Status.PUBLISHED;
import static java.util.stream.Collectors.toSet;

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
        this.addTask(new OpenTextTask(statement, order, this));
    }

    public void addSingleChoiceTask(String statement, Integer order, List<Option> options) {
        this.addTask(new SingleChoiceTask(statement, order, options, this));
    }

    public void addMultipleChoiceTask(String statement, Integer order, List<Option> options) {
        this.addTask(new MultipleChoiceTask(statement, order, options, this));
    }

    private void addTask(Task task) {
        Assert.isTrue(isOrderValidToInsert(task.getOrder()), "The order has to be in an insertable position.");
        insertNewTaskShiftingSubsequentTasks(task);
    }

    private boolean isOrderValidToInsert(Integer order) {
        return tasks.isEmpty()
                ? order.equals(1)
                : tasks.stream().map(Task::getOrder).anyMatch(o -> o.equals(order))
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

    public void publish() {
        this.status = PUBLISHED;
    }

    public boolean isPublished() {
        return this.status.equals(PUBLISHED);
    }

    public boolean hasAllTypeOfTasks() {
        Set<Type> foundTypes = this.tasks.stream().map(Task::getType).collect(toSet());
        return foundTypes.containsAll(EnumSet.allOf(Type.class));
    }

    public boolean hasAllTasksInValidOrder() {
        List<Integer> sortedOrders = tasks.stream().map(Task::getOrder).sorted().toList();
        for (int i = 0; i < sortedOrders.size(); i++) {
            if (sortedOrders.get(i) != i + 1) {
                return false;
            }
        }
        return true;
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
