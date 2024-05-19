package com.learning.springbootmongo;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Long generateSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1L); // Increment by 1L (Long)
        Sequence counter = mongoOperations.findAndModify(query, update, Sequence.class);
        if (counter == null) {
            counter = new Sequence(seqName, 1L);
            mongoOperations.save(counter);
        }
        return counter.getSeq();
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

        Workbook workbook = exportDataToExcel();
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public Workbook exportDataToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        List<String> columns = Arrays.asList("Id", "Name", "Email");

        // Create cell style for header
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Create header row
        Row headerRow = sheet.createRow(0);
        int index = 0;
        for (String column : columns) {
            Cell cell = headerRow.createCell(index);
            cell.setCellValue(column);
            cell.setCellStyle(headerStyle);
            index++;
        }

        List<StudentEntity> data = studentRepository.findAll();

        // Populate data rows
        int rowNum = 1;
        for (StudentEntity obj : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(obj.getId());
            row.createCell(1).setCellValue(obj.getName());
            row.createCell(2).setCellValue(obj.getEmail());
        }

        return workbook;
    }

}
