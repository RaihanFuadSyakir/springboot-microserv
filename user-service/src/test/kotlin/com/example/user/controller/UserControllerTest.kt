package com.example.user.controller

import com.example.user.model.User
import com.example.user.service.UserService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class UserControllerTest {
    
    @MockK
    private lateinit var userService: UserService
    
    private lateinit var userController: UserController
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        userController = UserController(userService)
    }
    
    @Test
    fun `should create user successfully`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        val savedUser = User(id = 1, username = "testuser", email = "test@example.com", password = "password")
        every { userService.createUser(any()) } returns savedUser
        
        // When
        val response = userController.createUser(user)
        
        // Then
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body == savedUser)
        verify { userService.createUser(match { it.username == "testuser" && it.email == "test@example.com" }) }
    }
    
    @Test
    fun `should return conflict when user creation fails due to duplicate`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        every { userService.createUser(any()) } throws RuntimeException("User already exists")
        
        // When
        val response = userController.createUser(user)
        
        // Then
        assert(response.statusCode == HttpStatus.CONFLICT)
        verify { userService.createUser(any()) }
    }
    
    @Test
    fun `should get user by ID successfully`() {
        // Given
        val id = 1L
        val user = User(id = id, username = "testuser", email = "test@example.com", password = "password")
        every { userService.getUserById(id) } returns Optional.of(user)
        
        // When
        val response = userController.getUserById(id)
        
        // Then
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == user)
        verify { userService.getUserById(id) }
    }
    
    @Test
    fun `should return not found when user does not exist`() {
        // Given
        val id = 1L
        every { userService.getUserById(id) } returns Optional.empty()
        
        // When
        val response = userController.getUserById(id)
        
        // Then
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        verify { userService.getUserById(id) }
    }
    
    @Test
    fun `should delete user successfully`() {
        // Given
        val id = 1L
        every { userService.deleteUser(id) } returns true
        
        // When
        val response = userController.deleteUser(id)
        
        // Then
        assert(response.statusCode == HttpStatus.NO_CONTENT)
        verify { userService.deleteUser(id) }
    }
    
    @Test
    fun `should return not found when deleting non-existent user`() {
        // Given
        val id = 1L
        every { userService.deleteUser(id) } returns false
        
        // When
        val response = userController.deleteUser(id)
        
        // Then
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        verify { userService.deleteUser(id) }
    }
}