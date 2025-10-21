package com.example.payment.controller

import com.example.payment.model.Payment
import com.example.payment.model.PaymentStatus
import com.example.payment.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    
    @PostMapping
    fun createPayment(@RequestBody payment: Payment): ResponseEntity<Payment> {
        val createdPayment = paymentService.createPayment(payment)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment)
    }
    
    @GetMapping("/{id}")
    fun getPaymentById(@PathVariable id: Long): ResponseEntity<Payment> {
        val payment = paymentService.getPaymentById(id)
        return if (payment.isPresent) {
            ResponseEntity.ok(payment.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/order/{orderId}")
    fun getPaymentByOrderId(@PathVariable orderId: Long): ResponseEntity<Payment> {
        val payment = paymentService.getPaymentByOrderId(orderId)
        return if (payment.isPresent) {
            ResponseEntity.ok(payment.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/user/{userId}")
    fun getPaymentsByUserId(@PathVariable userId: Long): ResponseEntity<List<Payment>> {
        val payments = paymentService.getPaymentsByUserId(userId)
        return ResponseEntity.ok(payments)
    }
    
    @GetMapping
    fun getAllPayments(): ResponseEntity<List<Payment>> {
        val payments = paymentService.getAllPayments()
        return ResponseEntity.ok(payments)
    }
    
    @PutMapping("/{id}")
    fun updatePayment(@PathVariable id: Long, @RequestBody payment: Payment): ResponseEntity<Payment> {
        val updatedPayment = paymentService.updatePayment(id, payment)
        return if (updatedPayment.isPresent) {
            ResponseEntity.ok(updatedPayment.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deletePayment(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = paymentService.deletePayment(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping("/process")
    fun processPayment(
        @RequestParam orderId: Long,
        @RequestParam userId: Long,
        @RequestParam amount: BigDecimal,
        @RequestParam paymentMethod: String
    ): ResponseEntity<Payment> {
        val payment = paymentService.processPayment(orderId, userId, amount, paymentMethod)
        return ResponseEntity.ok(payment)
    }
    
    @PutMapping("/{id}/status")
    fun updatePaymentStatus(@PathVariable id: Long, @RequestBody status: PaymentStatus): ResponseEntity<Payment> {
        val updatedPayment = paymentService.updatePaymentStatus(id, status)
        return if (updatedPayment.isPresent) {
            ResponseEntity.ok(updatedPayment.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PutMapping("/{id}/complete")
    fun completePayment(
        @PathVariable id: Long,
        @RequestParam transactionId: String
    ): ResponseEntity<Payment> {
        val completedPayment = paymentService.completePayment(id, transactionId)
        return if (completedPayment.isPresent) {
            ResponseEntity.ok(completedPayment.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PutMapping("/{id}/fail")
    fun failPayment(@PathVariable id: Long): ResponseEntity<Payment> {
        val failedPayment = paymentService.failPayment(id)
        return if (failedPayment.isPresent) {
            ResponseEntity.ok(failedPayment.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
}