package com.example.inventory.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inventory")
data class Inventory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(name = "product_id", nullable = false, unique = true)
    var productId: Long,
    
    @Column(nullable = false)
    var quantity: Int,
    
    @Column(name = "reserved_quantity", nullable = false)
    var reservedQuantity: Int = 0,
    
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // Default constructor for JPA
    constructor() : this(null, 0L, 0, 0, LocalDateTime.now(), LocalDateTime.now())
    
    fun availableQuantity(): Int {
        return quantity - reservedQuantity
    }
}