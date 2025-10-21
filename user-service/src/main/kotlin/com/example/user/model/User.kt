package com.example.user.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(nullable = false, unique = true)
    var username: String,
    
    @Column(nullable = false, unique = true)
    var email: String,
    
    @Column(nullable = false)
    var password: String,  // This will be stored as encrypted
    
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // Default constructor for JPA
    constructor() : this(null, "", "", "", LocalDateTime.now(), LocalDateTime.now())
}