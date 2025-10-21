package com.example.notification.controller

import com.example.notification.model.Notification
import com.example.notification.model.NotificationType
import com.example.notification.service.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
    
    @PostMapping
    fun createNotification(@RequestBody notification: Notification): ResponseEntity<Notification> {
        val createdNotification = notificationService.createNotification(notification)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification)
    }
    
    @GetMapping("/{id}")
    fun getNotificationById(@PathVariable id: Long): ResponseEntity<Notification> {
        val notification = notificationService.getNotificationById(id)
        return if (notification.isPresent) {
            ResponseEntity.ok(notification.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping("/send")
    fun sendNotification(
        @RequestParam recipient: String,
        @RequestParam type: NotificationType,
        @RequestParam subject: String,
        @RequestParam content: String
    ): ResponseEntity<Notification> {
        val notification = notificationService.sendNotification(recipient, type, subject, content)
        return ResponseEntity.ok(notification)
    }
    
    @PutMapping("/{id}/status")
    fun updateNotificationStatus(
        @PathVariable id: Long,
        @RequestParam status: String
    ): ResponseEntity<Notification> {
        // In a real implementation, this would update the notification status
        return ResponseEntity.notFound().build()
    }
}