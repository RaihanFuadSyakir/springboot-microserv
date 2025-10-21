package com.example.order.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(name = "user_id", nullable = false)
    var userId: Long,
    
    @Column(name = "product_id", nullable = false)
    var productId: Long,
    
    @Column(nullable = false)
    var quantity: Int,
    
    @Column(nullable = false)
    var price: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = OrderStatus.PENDING,
    
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // Default constructor for JPA
    constructor() : this(null, 0L, 0L, 0, BigDecimal.ZERO, OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now())
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}