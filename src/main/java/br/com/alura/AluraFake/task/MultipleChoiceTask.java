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
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends ChoiceTask {

    @Deprecated
    public MultipleChoiceTask() {}

    public MultipleChoiceTask(String statement, Integer order, List<Option> options, Course course) {
        super(statement, order, options, course, SINGLE_CHOICE);
        Assert.isTrue(hasAtLeastTwoCorrectOptions(options), "There must be at least two correct options.");
        Assert.isTrue(hasAtLeastOneIncorrectOption(options), "There must be at least one incorrect option.");
        this.options = options;
        options.forEach(option -> option.bindTask(this));
    }

    private boolean hasAtLeastTwoCorrectOptions(List<Option> options) {
        return options.stream().filter(Option::isCorrect).count() >= 2;
    }

    private boolean hasAtLeastOneIncorrectOption(List<Option> options) {
        return options.stream().anyMatch(option -> !option.isCorrect());
    }
}