package br.com.alura.AluraFake.task;

import jakarta.persistence.*;
import org.springframework.util.Assert;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name = "Options")
public class Option {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "option_text")
    private String option;

    @Column(name = "is_correct")
    private boolean isCorrect;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id")
    private Task task;

    @Deprecated
    public Option() {}

    public Option(String option, boolean isCorrect) {
        this.option = option;
        this.isCorrect = isCorrect;
    }

    public void bindTask(Task taskToBind) {
        Assert.isNull(task, "Option already bound to a task.");
        this.task = taskToBind;
    }

    public String getOption() {
        return option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}