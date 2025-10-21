package com.example.user.integration

import com.example.user.UserApplication
import com.example.user.model.User
import com.example.user.repository.UserRepository
import com.example.user.service.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = ["spring.datasource.url=jdbc:h2:mem:testdb", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password="])
class UserRepositoryIntegrationTest {
    
    @Autowired
    private lateinit var userRepository: UserRepository
    
    @Test
    fun `should save and find user by email`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        
        // When
        val savedUser = userRepository.save(user)
        val foundUser = userRepository.findByEmail("test@example.com")
        
        // Then
        assert(foundUser.isPresent)
        assert(foundUser.get().id == savedUser.id)
        assert(foundUser.get().email == "test@example.com")
    }
    
    @Test
    fun `should save and find user by username`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        
        // When
        val savedUser = userRepository.save(user)
        val foundUser = userRepository.findByUsername("testuser")
        
        // Then
        assert(foundUser.isPresent)
        assert(foundUser.get().id == savedUser.id)
        assert(foundUser.get().username == "testuser")
    }
}

@SpringBootTest(classes = [UserApplication::class])
@ContextConfiguration(classes = [UserApplication::class])
class UserServiceIntegrationTest {
    
    @Autowired
    private lateinit var userService: UserService
    
    @Autowired
    private lateinit var userRepository: UserRepository
    
    @Test
    fun `should create and find user through service layer`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        
        // When
        val createdUser = userService.createUser(user)
        val foundUser = userService.findByEmail("test@example.com")
        
        // Then
        assert(foundUser.isPresent)
        assert(foundUser.get().id == createdUser.id)
        assert(foundUser.get().email == "test@example.com")
        
        // Cleanup
        userRepository.delete(createdUser)
    }
    
    @Test
    fun `should not create user with duplicate email`() {
        // Given
        val firstUser = User(username = "testuser1", email = "test@example.com", password = "password")
        val secondUser = User(username = "testuser2", email = "test@example.com", password = "password")
        
        try {
            // When
            userService.createUser(firstUser)
            
            // Then
            org.junit.jupiter.api.assertThrows<RuntimeException> {
                userService.createUser(secondUser)
            }
        } finally {
            // Cleanup
            userRepository.deleteAll()
        }
    }
}