package com.example.user.service

import com.example.user.model.User
import com.example.user.repository.UserRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

class UserServiceTest {
    
    @MockK
    private lateinit var userRepository: UserRepository
    
    private lateinit var userService: UserService
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        userService = UserService(userRepository)
    }
    
    @Test
    fun `should create user when email and username are unique`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        val savedUser = User(id = 1, username = "testuser", email = "test@example.com", password = "password")
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        every { userRepository.findByUsername(any()) } returns Optional.empty()
        every { userRepository.save(any()) } returns savedUser
        
        // When
        val result = userService.createUser(user)
        
        // Then
        assertEquals(savedUser, result)
        verify { userRepository.findByEmail("test@example.com") }
        verify { userRepository.findByUsername("testuser") }
        verify { userRepository.save(match { it.username == "testuser" && it.email == "test@example.com" }) }
    }
    
    @Test
    fun `should throw exception when email already exists`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        val existingUser = User(id = 1, username = "otheruser", email = "test@example.com", password = "password")
        every { userRepository.findByEmail("test@example.com") } returns Optional.of(existingUser)
        
        // When/Then
        assertThrows(RuntimeException::class.java) {
            userService.createUser(user)
        }
        verify { userRepository.findByEmail("test@example.com") }
        verify(exactly = 0) { userRepository.findByUsername(any()) }
    }
    
    @Test
    fun `should throw exception when username already exists`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        val existingUser = User(id = 1, username = "testuser", email = "other@example.com", password = "password")
        every { userRepository.findByEmail(any()) } returns Optional.empty()
        every { userRepository.findByUsername("testuser") } returns Optional.of(existingUser)
        
        // When/Then
        assertThrows(RuntimeException::class.java) {
            userService.createUser(user)
        }
        verify { userRepository.findByEmail("test@example.com") }
        verify { userRepository.findByUsername("testuser") }
    }
    
    @Test
    fun `should find user by id`() {
        // Given
        val id = 1L
        val user = User(id = id, username = "testuser", email = "test@example.com", password = "password")
        every { userRepository.findById(id) } returns Optional.of(user)
        
        // When
        val result = userService.getUserById(id)
        
        // Then
        assertTrue(result.isPresent)
        assertEquals(user, result.get())
        verify { userRepository.findById(id) }
    }
    
    @Test
    fun `should return empty optional when user not found by id`() {
        // Given
        val id = 1L
        every { userRepository.findById(id) } returns Optional.empty()
        
        // When
        val result = userService.getUserById(id)
        
        // Then
        assertFalse(result.isPresent)
        verify { userRepository.findById(id) }
    }
    
    @Test
    fun `should delete user successfully`() {
        // Given
        val id = 1L
        every { userRepository.existsById(id) } returns true
        every { userRepository.deleteById(id) } just Runs
        
        // When
        val result = userService.deleteUser(id)
        
        // Then
        assertTrue(result)
        verify { userRepository.existsById(id) }
        verify { userRepository.deleteById(id) }
    }
    
    @Test
    fun `should not delete user if not exists`() {
        // Given
        val id = 1L
        every { userRepository.existsById(id) } returns false
        
        // When
        val result = userService.deleteUser(id)
        
        // Then
        assertFalse(result)
        verify { userRepository.existsById(id) }
        verify(exactly = 0) { userRepository.deleteById(any()) }
    }
}