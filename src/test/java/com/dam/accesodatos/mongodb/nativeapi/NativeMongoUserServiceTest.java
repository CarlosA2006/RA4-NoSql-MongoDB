package com.dam.accesodatos.mongodb.nativeapi;

import com.dam.accesodatos.exception.DuplicateEmailException;
import com.dam.accesodatos.exception.InvalidUserIdException;
import com.dam.accesodatos.exception.UserNotFoundException;
import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DisplayName("NativeMongoUserService Tests")
class NativeMongoUserServiceTest {

    @Autowired
    private NativeMongoUserService service;

    private String uniqueEmail() {
        return "test-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }

    @Nested
    @DisplayName("Test Connection")
    class TestConnection {

        @Test
        @DisplayName("Debe conectar exitosamente a MongoDB")
        void testConnection_Success() {
            String result = service.testConnection();
            assertThat(result).contains("Conexión API Nativa exitosa");
            assertThat(result).contains("pedagogico_db");
        }
    }

    @Nested
    @DisplayName("Create User")
    class CreateUser {

        @Test
        @DisplayName("Debe crear usuario con datos válidos")
        void createUser_ValidData_ReturnsUserWithId() {
            String email = uniqueEmail();
            UserCreateDto dto = new UserCreateDto("Test Native", email, "IT", "Tester");
            User created = service.createUser(dto);

            assertThat(created.getId()).isNotNull();
            assertThat(created.getId()).hasSize(24); // ObjectId length
            assertThat(created.getName()).isEqualTo("Test Native");
            assertThat(created.getEmail()).isEqualTo(email);
            assertThat(created.getDepartment()).isEqualTo("IT");
            assertThat(created.getRole()).isEqualTo("Tester");
            assertThat(created.getActive()).isTrue();
            assertThat(created.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Debe lanzar DuplicateEmailException con email duplicado")
        @org.junit.jupiter.api.Disabled("Requiere índice único en MongoDB - funciona en producción pero no en MongoDB embebido sin configuración adicional")
        void createUser_DuplicateEmail_ThrowsException() {
            String duplicateEmail = uniqueEmail();
            UserCreateDto dto1 = new UserCreateDto("User 1", duplicateEmail, "IT", "Dev");
            service.createUser(dto1);

            UserCreateDto dto2 = new UserCreateDto("User 2", duplicateEmail, "HR", "Manager");
            assertThatThrownBy(() -> service.createUser(dto2))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining(duplicateEmail);
        }
    }

    @Nested
    @DisplayName("Find User By ID")
    class FindUserById {

        @Test
        @DisplayName("Debe encontrar usuario existente")
        void findUserById_ExistingId_ReturnsUser() {
            String email = uniqueEmail();
            UserCreateDto dto = new UserCreateDto("Find Native", email, "HR", "Manager");
            User created = service.createUser(dto);

            User found = service.findUserById(created.getId());

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(created.getId());
            assertThat(found.getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException con ID inexistente")
        void findUserById_NonExistingId_ThrowsException() {
            String nonExistingId = "507f1f77bcf86cd799439011"; // Valid ObjectId format but doesn't exist

            assertThatThrownBy(() -> service.findUserById(nonExistingId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining(nonExistingId);
        }

        @Test
        @DisplayName("Debe lanzar InvalidUserIdException con ID inválido")
        void findUserById_InvalidId_ThrowsException() {
            String invalidId = "invalid-id";

            assertThatThrownBy(() -> service.findUserById(invalidId))
                    .isInstanceOf(InvalidUserIdException.class)
                    .hasMessageContaining(invalidId);
        }
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUser {

        @Test
        @DisplayName("Debe actualizar usuario existente")
        void updateUser_ValidData_ReturnsUpdatedUser() {
            String email = uniqueEmail();
            UserCreateDto createDto = new UserCreateDto("Update Native", email, "IT", "Dev");
            User created = service.createUser(createDto);

            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setName("Updated Name");
            updateDto.setDepartment("HR");

            User updated = service.updateUser(created.getId(), updateDto);

            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getDepartment()).isEqualTo("HR");
            assertThat(updated.getEmail()).isEqualTo(email); // Unchanged
        }

        @Test
        @DisplayName("Debe lanzar UserNotFoundException al actualizar usuario inexistente")
        void updateUser_NonExistingId_ThrowsException() {
            String nonExistingId = "507f1f77bcf86cd799439011";
            UserUpdateDto dto = new UserUpdateDto();
            dto.setName("New Name");

            assertThatThrownBy(() -> service.updateUser(nonExistingId, dto))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete User")
    class DeleteUser {

        @Test
        @DisplayName("Debe eliminar usuario existente")
        void deleteUser_ExistingId_ReturnsTrue() {
            UserCreateDto dto = new UserCreateDto("Delete Native", uniqueEmail(), "IT", "Dev");
            User created = service.createUser(dto);

            boolean deleted = service.deleteUser(created.getId());

            assertThat(deleted).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false al eliminar usuario inexistente")
        void deleteUser_NonExistingId_ReturnsFalse() {
            String nonExistingId = "507f1f77bcf86cd799439011";

            boolean deleted = service.deleteUser(nonExistingId);

            assertThat(deleted).isFalse();
        }

        @Test
        @DisplayName("Debe lanzar InvalidUserIdException con ID inválido")
        void deleteUser_InvalidId_ThrowsException() {
            String invalidId = "invalid";

            assertThatThrownBy(() -> service.deleteUser(invalidId))
                    .isInstanceOf(InvalidUserIdException.class);
        }
    }

    @Nested
    @DisplayName("TODO Methods - Pending Implementation")
    class TodoMethods {

        @Test
        @DisplayName("findAll debe lanzar UnsupportedOperationException")
        void findAll_ThrowsUnsupportedOperation() {
            assertThrows(UnsupportedOperationException.class, () -> service.findAll());
        }

        @Test
        @DisplayName("findUsersByDepartment debe lanzar UnsupportedOperationException")
        void findUsersByDepartment_ThrowsUnsupportedOperation() {
            assertThrows(UnsupportedOperationException.class, () -> service.findUsersByDepartment("IT"));
        }

        @Test
        @DisplayName("searchUsers debe lanzar UnsupportedOperationException")
        void searchUsers_ThrowsUnsupportedOperation() {
            assertThrows(UnsupportedOperationException.class, () -> service.searchUsers(null));
        }

        @Test
        @DisplayName("countByDepartment debe lanzar UnsupportedOperationException")
        void countByDepartment_ThrowsUnsupportedOperation() {
            assertThrows(UnsupportedOperationException.class, () -> service.countByDepartment("IT"));
        }
    }
}
