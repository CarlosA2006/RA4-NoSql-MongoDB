package com.dam.accesodatos.mongodb.nativeapi;

import com.dam.accesodatos.model.DepartmentStatsDto;
import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserQueryDto;
import com.dam.accesodatos.model.UserUpdateDto;

import java.util.List;

public interface NativeMongoUserService {

    String testConnection();

    User createUser(UserCreateDto dto);

    User findUserById(String id);

    User updateUser(String id, UserUpdateDto dto);

    boolean deleteUser(String id);

    List<User> findAll();

    List<User> findUsersByDepartment(String department);

    List<User> searchUsers(UserQueryDto query);

    long countByDepartment(String department);

    /**
     * Ejemplo de Aggregation Pipeline: Obtiene estadísticas de usuarios por departamento.
     * Este método demuestra el uso del framework de agregación de MongoDB.
     *
     * @return Lista de estadísticas por departamento
     */
    List<DepartmentStatsDto> getStatsByDepartment();
}
