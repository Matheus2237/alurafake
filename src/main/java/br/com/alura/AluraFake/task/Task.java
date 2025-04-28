package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;

import static jakarta.persistence.DiscriminatorType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = STRING)
public abstract class Task implements Comparable<Task> {

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

    @Override
    public int compareTo(Task other) {
        return this.order.compareTo(other.order);
    }

    public void incrementOrder() {
        this.order++;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrder() {
        return order;
    }

    public Course getCourse() {
        return course;
    }
}