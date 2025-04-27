package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;

import java.util.Objects;

import static jakarta.persistence.DiscriminatorType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = STRING)
public abstract class Task {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String statement;

    @Column(name = "task_order", nullable = false)
    private Integer order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(insertable = false, updatable = false)
    private Type type;

    protected Task(String statement, Integer order, Course course, Type type) {
        this.statement = statement;
        this.order = order;
        this.course = course;
        this.type = type;
    }

    public Course getCourse() {
        return course;
    }

    public String getStatement() {
        return statement;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id)
                && Objects.equals(statement, task.statement)
                && Objects.equals(order, task.order)
                && Objects.equals(course, task.course)
                && Objects.equals(type, task.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statement, order, course);
    }
}