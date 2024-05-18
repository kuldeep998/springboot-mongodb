package com.learning.springbootmongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @GetMapping("/health")
    public ResponseEntity<String> defaultRoute() {
        return ResponseEntity.ok("Welcome to MongoDB Application");
    }

    @GetMapping
    public ResponseEntity<List<StudentEntity>> getAllStudent() {
        List<StudentEntity> student = studentRepository.findAll();
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentEntity> getStudentById(@PathVariable Long id) {
        StudentEntity student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            return new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<StudentEntity> createStudent(@RequestBody StudentEntity student) {
        student.setId(generateSequence(student.SEQUENCE_NAME));
        StudentEntity createdStudent = studentRepository.save(student);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public Long generateSequence(String seqName){
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1L); // Increment by 1L (Long)
        Sequence counter = mongoOperations.findAndModify(query, update, Sequence.class);
        if (counter == null) {
            counter = new Sequence(seqName, 1L);
            mongoOperations.save(counter);
        }
        return counter.getSeq();
    }

}
