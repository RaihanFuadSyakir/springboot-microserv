package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {
    
    @Mock
    private UserService userService;
    
    @InjectMocks
    private UserController userController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        User savedUser = new User("testuser", "test@example.com", "password");
        savedUser.setId(1L);
        
        when(userService.createUser(any(User.class))).thenReturn(savedUser);
        
        // When
        ResponseEntity<User> response = userController.createUser(user);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        
        verify(userService).createUser(any(User.class));
    }
    
    @Test
    void shouldReturnConflictWhenUserCreationFails() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("User already exists"));
        
        // When
        ResponseEntity<User> response = userController.createUser(user);
        
        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userService).createUser(any(User.class));
    }
    
    @Test
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long id = 1L;
        User user = new User("testuser", "test@example.com", "password");
        user.setId(id);
        
        when(userService.getUserById(id)).thenReturn(Optional.of(user));
        
        // When
        ResponseEntity<User> response = userController.getUserById(id);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        
        verify(userService).getUserById(id);
    }
    
    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        // Given
        Long id = 1L;
        
        when(userService.getUserById(id)).thenReturn(Optional.empty());
        
        // When
        ResponseEntity<User> response = userController.getUserById(id);
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService).getUserById(id);
    }
}