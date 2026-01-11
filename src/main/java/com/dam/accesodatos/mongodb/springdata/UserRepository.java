package com.dam.accesodatos.mongodb.springdata;

import com.dam.accesodatos.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    List<User> findByDepartment(String department);
    
    long countByDepartment(String department);
    
    List<User> findByActive(Boolean active);
    
    List<User> findByNameContainingIgnoreCase(String name);
}
