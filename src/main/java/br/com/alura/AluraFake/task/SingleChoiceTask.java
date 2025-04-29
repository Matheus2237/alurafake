package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import org.springframework.util.Assert;

import java.util.List;

import static br.com.alura.AluraFake.task.Type.SINGLE_CHOICE;
import static jakarta.persistence.CascadeType.ALL;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends ChoiceTask {

    @Deprecated
    public SingleChoiceTask() {}

    public SingleChoiceTask(String statement, Integer order, List<Option> options, Course course) {
        super(statement, order, options, course, SINGLE_CHOICE);
        Assert.isTrue(hasOnlyOneCorrectAnswer(options), "There must be exactly one correct option.");
        this.options = options;
        options.forEach(option -> option.bindTask(this));
    }

    private boolean hasOnlyOneCorrectAnswer(List<Option> options) {
        return options.stream().filter(Option::isCorrect).count() == 1;
    }
}