package com.example.user.system

import com.example.user.UserApplication
import com.example.user.model.User
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.junit.jupiter.api.Assertions.*

@SpringBootTest(classes = [UserApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceSystemTest {
    
    @LocalServerPort
    private var port: Int = 0
    
    private val restTemplate = RestTemplate()
    
    @Test
    fun `should create and retrieve user through REST API`() {
        val baseUrl = "http://localhost:$port/api/users"
        
        // Create a user
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val user = User(username = "systemtestuser", email = "systemtest@example.com", password = "password")
        val request = HttpEntity(user, headers)
        
        val response = restTemplate.postForEntity(baseUrl, request, User::class.java)
        assertTrue(response.statusCode.is2xxSuccessful)
        
        val createdUser = response.body
        assertNotNull(createdUser)
        assertEquals("systemtestuser", createdUser?.username)
        
        // Retrieve the user by ID
        val userId = createdUser?.id
        assertNotNull(userId)
        
        val getUserResponse = restTemplate.getForEntity("$baseUrl/$userId", User::class.java)
        assertTrue(getUserResponse.statusCode.is2xxSuccessful)
        
        val retrievedUser = getUserResponse.body
        assertNotNull(retrievedUser)
        assertEquals(userId, retrievedUser?.id)
        assertEquals("systemtestuser", retrievedUser?.username)
        assertEquals("systemtest@example.com", retrievedUser?.email)
    }
    
    @Test
    fun `should return 404 for non-existent user`() {
        val baseUrl = "http://localhost:$port/api/users/999999"
        
        val response = restTemplate.getForEntity(baseUrl, String::class.java)
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, response.statusCode)
    }
}