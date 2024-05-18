package com.learning.springbootmongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface StudentRepository extends MongoRepository<StudentEntity,Long>
{
}
