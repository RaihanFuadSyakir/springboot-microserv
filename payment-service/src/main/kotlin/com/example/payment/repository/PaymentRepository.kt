package com.example.payment.repository

import com.example.payment.model.Payment
import com.example.payment.model.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findByOrderId(orderId: Long): Optional<Payment>
    fun findByUserId(userId: Long): List<Payment>
    fun findByStatus(status: PaymentStatus): List<Payment>
    fun findByTransactionId(transactionId: String): Optional<Payment>
}