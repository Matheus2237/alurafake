package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import static br.com.alura.AluraFake.task.Type.OPEN_TEXT;

@Entity
@DiscriminatorValue("OPEN_TEXT")
public class OpenTextTask extends Task {

    @Deprecated
    public OpenTextTask() {}

    public OpenTextTask(String statement, Integer order, Course course) {
        super(statement, order, course, OPEN_TEXT);
    }
}
