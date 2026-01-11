package com.dam.accesodatos.mongodb.springdata;

import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserQueryDto;
import com.dam.accesodatos.model.UserUpdateDto;

import java.util.List;

public interface SpringDataUserService {

    String testConnection();

    User createUser(UserCreateDto dto);

    User findUserById(String id);

    User updateUser(String id, UserUpdateDto dto);

    boolean deleteUser(String id);

    List<User> findAll();

    List<User> findUsersByDepartment(String department);

    List<User> searchUsers(UserQueryDto query);

    long countByDepartment(String department);
}
