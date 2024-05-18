package com.learning.springbootmongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "sequences")

@NoArgsConstructor
@Data
@ToString
public class Sequence {
    @Id
    private String id;
    private Long seq;

    public Sequence(String id, Long seq) {
        this.id = id;
        this.seq = seq;
    }
}
