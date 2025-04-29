CREATE TABLE Task (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    statement VARCHAR(255) NOT NULL,
    task_order INT NOT NULL,
    course_id bigint(20) NOT NULL,
    type ENUM('OPEN_TEXT', 'MULTIPLE_CHOICE', 'SINGLE_CHOICE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Course FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

CREATE TABLE Options (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    option_text VARCHAR(80) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    task_id bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Task FOREIGN KEY (task_id) REFERENCES Task(id) ON DELETE CASCADE
);