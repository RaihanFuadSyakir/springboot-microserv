package com.example.user.service

import com.example.user.model.User
import com.example.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(user: User): User {
        // Check if user with email or username already exists
        if (userRepository.findByEmail(user.email).isPresent) {
            throw RuntimeException("User with email ${user.email} already exists")
        }
        if (userRepository.findByUsername(user.username).isPresent) {
            throw RuntimeException("User with username ${user.username} already exists")
        }
        return userRepository.save(user)
    }
    
    fun getUserById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }
    
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }
    
    fun updateUser(id: Long, user: User): Optional<User> {
        return if (userRepository.existsById(id)) {
            val userToUpdate = user.copy(id = id)
            Optional.of(userRepository.save(userToUpdate))
        } else {
            Optional.empty()
        }
    }
    
    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
    
    fun findByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }
    
    fun findByUsername(username: String): Optional<User> {
        return userRepository.findByUsername(username)
    }
}