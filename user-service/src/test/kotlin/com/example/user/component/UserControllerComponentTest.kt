package com.example.user.component

import com.example.user.UserApplication
import com.example.user.controller.UserController
import com.example.user.model.User
import com.example.user.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import java.util.*

@WebMvcTest(UserController::class)
@ContextConfiguration(classes = [UserApplication::class])
class UserControllerComponentTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var userService: UserService
    
    @Autowired
    private lateinit var objectMapper: ObjectMapper
    
    @Test
    fun `should create user successfully`() {
        // Given
        val user = User(username = "testuser", email = "test@example.com", password = "password")
        val savedUser = User(id = 1, username = "testuser", email = "test@example.com", password = "password")
        
        whenever(userService.createUser(any())).thenReturn(savedUser)
        
        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.username").value("testuser"))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.email").value("test@example.com"))
    }
    
    @Test
    fun `should get user by id`() {
        // Given
        val id = 1L
        val user = User(id = id, username = "testuser", email = "test@example.com", password = "password")
        
        whenever(userService.getUserById(id)).thenReturn(Optional.of(user))
        
        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(id))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.username").value("testuser"))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.email").value("test@example.com"))
    }
    
    @Test
    fun `should return not found for non-existent user`() {
        // Given
        val id = 1L
        
        whenever(userService.getUserById(id)).thenReturn(Optional.empty())
        
        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
    
    @Test
    fun `should delete user successfully`() {
        // Given
        val id = 1L
        
        whenever(userService.deleteUser(id)).thenReturn(true)
        
        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }
    
    @Test
    fun `should return not found when deleting non-existent user`() {
        // Given
        val id = 1L
        
        whenever(userService.deleteUser(id)).thenReturn(false)
        
        // When & Then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }
}