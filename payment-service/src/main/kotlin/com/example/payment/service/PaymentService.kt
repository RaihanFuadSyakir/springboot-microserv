package com.example.payment.service

import com.example.payment.model.Payment
import com.example.payment.model.PaymentStatus
import com.example.payment.repository.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
@Transactional
class PaymentService(
    private val paymentRepository: PaymentRepository
) {
    fun createPayment(payment: Payment): Payment {
        // Set initial status to PENDING
        val paymentToSave = payment.copy(status = PaymentStatus.PENDING)
        return paymentRepository.save(paymentToSave)
    }
    
    fun getPaymentById(id: Long): Optional<Payment> {
        return paymentRepository.findById(id)
    }
    
    fun getPaymentByOrderId(orderId: Long): Optional<Payment> {
        return paymentRepository.findByOrderId(orderId)
    }
    
    fun getPaymentsByUserId(userId: Long): List<Payment> {
        return paymentRepository.findByUserId(userId)
    }
    
    fun getAllPayments(): List<Payment> {
        return paymentRepository.findAll()
    }
    
    fun updatePayment(id: Long, payment: Payment): Optional<Payment> {
        return if (paymentRepository.existsById(id)) {
            val paymentToUpdate = payment.copy(id = id, updatedAt = java.time.LocalDateTime.now())
            Optional.of(paymentRepository.save(paymentToUpdate))
        } else {
            Optional.empty()
        }
    }
    
    fun deletePayment(id: Long): Boolean {
        return if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id)
            true
        } else {
            false
        }
    }
    
    fun processPayment(orderId: Long, userId: Long, amount: BigDecimal, paymentMethod: String): Payment {
        val existingPayment = paymentRepository.findByOrderId(orderId)
        if (existingPayment.isPresent) {
            throw RuntimeException("Payment for order $orderId already exists")
        }
        
        val payment = Payment(
            orderId = orderId,
            userId = userId,
            amount = amount,
            status = PaymentStatus.PROCESSING,
            paymentMethod = paymentMethod
        )
        
        return paymentRepository.save(payment)
    }
    
    fun updatePaymentStatus(id: Long, status: PaymentStatus): Optional<Payment> {
        val paymentOptional = paymentRepository.findById(id)
        return if (paymentOptional.isPresent) {
            val payment = paymentOptional.get()
            val updatedPayment = payment.copy(status = status, updatedAt = java.time.LocalDateTime.now())
            Optional.of(paymentRepository.save(updatedPayment))
        } else {
            Optional.empty()
        }
    }
    
    fun completePayment(id: Long, transactionId: String): Optional<Payment> {
        val paymentOptional = paymentRepository.findById(id)
        return if (paymentOptional.isPresent) {
            val payment = paymentOptional.get()
            if (payment.status != PaymentStatus.COMPLETED && payment.status != PaymentStatus.FAILED) {
                val updatedPayment = payment.copy(
                    status = PaymentStatus.COMPLETED,
                    transactionId = transactionId,
                    updatedAt = java.time.LocalDateTime.now()
                )
                Optional.of(paymentRepository.save(updatedPayment))
            } else {
                Optional.empty()  // Cannot update if already completed or failed
            }
        } else {
            Optional.empty()
        }
    }
    
    fun failPayment(id: Long): Optional<Payment> {
        val paymentOptional = paymentRepository.findById(id)
        return if (paymentOptional.isPresent) {
            val payment = paymentOptional.get()
            if (payment.status != PaymentStatus.COMPLETED) {
                val updatedPayment = payment.copy(
                    status = PaymentStatus.FAILED,
                    updatedAt = java.time.LocalDateTime.now()
                )
                Optional.of(paymentRepository.save(updatedPayment))
            } else {
                Optional.empty()  // Cannot fail if already completed
            }
        } else {
            Optional.empty()
        }
    }
}