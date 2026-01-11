package com.dam.accesodatos.controller;

import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserQueryDto;
import com.dam.accesodatos.model.UserUpdateDto;
import com.dam.accesodatos.mongodb.springdata.SpringDataUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/springdata")
@Tag(name = "Spring Data", description = "Endpoints usando Spring Data MongoDB")
public class SpringDataController {

    private final SpringDataUserService userService;

    @Autowired
    public SpringDataController(SpringDataUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test-connection")
    @Operation(summary = "Probar conexión", description = "Verifica la conexión con MongoDB usando MongoTemplate")
    @ApiResponse(responseCode = "200", description = "Conexión exitosa")
    public ResponseEntity<Map<String, String>> testConnection() {
        String result = userService.testConnection();
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/users")
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario usando MongoRepository")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateDto dto) {
        User user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Buscar por ID", description = "Obtiene un usuario usando findById de MongoRepository")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> findUserById(
            @Parameter(description = "ID del usuario") @PathVariable String id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario usando save de MongoRepository")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID del usuario") @PathVariable String id,
            @Valid @RequestBody UserUpdateDto dto) {
        User user = userService.updateUser(id, dto);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario usando deleteById de MongoRepository")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario") @PathVariable String id) {
        boolean deleted = userService.deleteUser(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Listar todos (TODO)", description = "Lista todos los usuarios. PENDIENTE: Los estudiantes deben usar findAll() de MongoRepository")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/department/{department}")
    @Operation(summary = "Buscar por departamento (TODO)", description = "Filtra usuarios por departamento. PENDIENTE: Los estudiantes deben usar Query Methods derivados")
    public ResponseEntity<List<User>> findUsersByDepartment(
            @Parameter(description = "Nombre del departamento") @PathVariable String department) {
        List<User> users = userService.findUsersByDepartment(department);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/search")
    @Operation(summary = "Búsqueda avanzada (TODO)", description = "Búsqueda con filtros y paginación. PENDIENTE: Los estudiantes deben usar MongoTemplate con Criteria")
    public ResponseEntity<List<User>> searchUsers(@RequestBody UserQueryDto query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/count/department/{department}")
    @Operation(summary = "Contar por departamento (TODO)", description = "Cuenta usuarios por departamento. PENDIENTE: Los estudiantes deben usar countByDepartment del Repository")
    public ResponseEntity<Map<String, Object>> countByDepartment(
            @Parameter(description = "Nombre del departamento") @PathVariable String department) {
        long count = userService.countByDepartment(department);
        return ResponseEntity.ok(Map.of("department", department, "count", count));
    }
}
