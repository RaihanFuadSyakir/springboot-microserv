package com.example.payment.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(name = "order_id", nullable = false)
    var orderId: Long,
    
    @Column(name = "user_id", nullable = false)
    var userId: Long,
    
    @Column(nullable = false)
    var amount: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus,
    
    @Column(name = "payment_method", nullable = false)
    var paymentMethod: String,  // e.g., CREDIT_CARD, DEBIT_CARD, PAYPAL, etc.
    
    @Column(name = "transaction_id")
    var transactionId: String? = null,  // External transaction ID from payment gateway
    
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // Default constructor for JPA
    constructor() : this(null, 0L, 0L, BigDecimal.ZERO, PaymentStatus.PENDING, "", null, LocalDateTime.now(), LocalDateTime.now())
}

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED
}