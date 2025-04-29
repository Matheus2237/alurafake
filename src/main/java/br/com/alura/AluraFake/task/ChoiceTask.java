package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import org.springframework.util.Assert;

import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@MappedSuperclass
public class ChoiceTask extends Task {

    @OneToMany(mappedBy = "task", cascade = ALL, orphanRemoval = true)
    protected List<Option> options;

    @Deprecated
    public ChoiceTask() {}

    public ChoiceTask(String statement, Integer order, List<Option> options, Course course, Type type) {
        super(statement, order, course, type);
        Assert.isTrue(hasUniqueOptions(options), "There must be at least two correct options.");
        Assert.isTrue(isAllOptionsDifferentFromStatement(options), "Options must be different from the statement.");
    }

    private boolean hasUniqueOptions(List<Option> options) {
        return options.stream().map(Option::getOption).distinct().count() == options.size();
    }

    private boolean isAllOptionsDifferentFromStatement(List<Option> options) {
        return options.stream().map(Option::getOption).noneMatch(s -> s.equals(statement));
    }

    public List<Option> getOptions() {
        return options;
    }
}