package com.learning.springbootmongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
@Data
public class StudentEntity {
    public static final String SEQUENCE_NAME = "student_sequence";


    @Id
    private Long id;
    private String name;
    private String email;
}
