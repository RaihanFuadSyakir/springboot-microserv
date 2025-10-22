package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void shouldCreateUserWhenEmailAndUsernameAreUnique() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        User savedUser = new User("testuser", "test@example.com", "password");
        savedUser.setId(1L);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        User result = userService.createUser(user);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        User existingUser = new User("otheruser", "test@example.com", "password");
        existingUser.setId(1L);
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });
        
        assertEquals("User with email test@example.com already exists", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).findByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldGetUserById() {
        // Given
        Long id = 1L;
        User user = new User("testuser", "test@example.com", "password");
        user.setId(id);
        
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        
        // When
        Optional<User> result = userService.getUserById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        
        verify(userRepository).findById(id);
    }
}