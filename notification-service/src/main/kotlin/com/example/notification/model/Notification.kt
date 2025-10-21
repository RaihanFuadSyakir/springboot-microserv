package com.example.notification.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(nullable = false)
    var recipient: String,  // Email address or phone number depending on type
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: NotificationType,
    
    @Column(nullable = false)
    var subject: String,
    
    @Lob
    @Column(nullable = false)
    var content: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: NotificationStatus = NotificationStatus.PENDING,
    
    @Column(name = "sent_at")
    var sentAt: LocalDateTime? = null,
    
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // Default constructor for JPA
    constructor() : this(null, "", NotificationType.EMAIL, "", "", NotificationStatus.PENDING, null, LocalDateTime.now(), LocalDateTime.now())
}

enum class NotificationType {
    EMAIL,
    SMS
}

enum class NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    DELIVERED
}