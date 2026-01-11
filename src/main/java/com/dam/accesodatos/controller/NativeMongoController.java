package com.dam.accesodatos.controller;

import com.dam.accesodatos.model.DepartmentStatsDto;
import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserQueryDto;
import com.dam.accesodatos.model.UserUpdateDto;
import com.dam.accesodatos.mongodb.nativeapi.NativeMongoUserService;
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
@RequestMapping("/api/native")
@Tag(name = "API Nativa", description = "Endpoints usando el driver nativo de MongoDB")
public class NativeMongoController {

    private final NativeMongoUserService userService;

    @Autowired
    public NativeMongoController(NativeMongoUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test-connection")
    @Operation(summary = "Probar conexión", description = "Verifica la conexión con MongoDB y muestra información de la base de datos")
    @ApiResponse(responseCode = "200", description = "Conexión exitosa")
    public ResponseEntity<Map<String, String>> testConnection() {
        String result = userService.testConnection();
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/users")
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en la base de datos")
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
    @Operation(summary = "Buscar por ID", description = "Obtiene un usuario por su ID de MongoDB")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    public ResponseEntity<User> findUserById(
            @Parameter(description = "ID del usuario (ObjectId de 24 caracteres hex)") @PathVariable String id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
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
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario de la base de datos")
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
    @Operation(summary = "Listar todos (TODO)", description = "Lista todos los usuarios. PENDIENTE: Los estudiantes deben implementar este método")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/department/{department}")
    @Operation(summary = "Buscar por departamento (TODO)", description = "Filtra usuarios por departamento. PENDIENTE: Los estudiantes deben implementar este método")
    public ResponseEntity<List<User>> findUsersByDepartment(
            @Parameter(description = "Nombre del departamento (IT, HR, Finance, etc.)") @PathVariable String department) {
        List<User> users = userService.findUsersByDepartment(department);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/search")
    @Operation(summary = "Búsqueda avanzada (TODO)", description = "Búsqueda con filtros y paginación. PENDIENTE: Los estudiantes deben implementar este método")
    public ResponseEntity<List<User>> searchUsers(@RequestBody UserQueryDto query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/count/department/{department}")
    @Operation(summary = "Contar por departamento (TODO)", description = "Cuenta usuarios por departamento. PENDIENTE: Los estudiantes deben implementar este método")
    public ResponseEntity<Map<String, Object>> countByDepartment(
            @Parameter(description = "Nombre del departamento") @PathVariable String department) {
        long count = userService.countByDepartment(department);
        return ResponseEntity.ok(Map.of("department", department, "count", count));
    }

    @GetMapping("/stats/departments")
    @Operation(summary = "Estadísticas por departamento (Aggregation Pipeline)",
            description = "Ejemplo de Aggregation Pipeline de MongoDB. Agrupa usuarios por departamento y calcula totales y activos.")
    @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    public ResponseEntity<List<DepartmentStatsDto>> getStatsByDepartment() {
        List<DepartmentStatsDto> stats = userService.getStatsByDepartment();
        return ResponseEntity.ok(stats);
    }
}
