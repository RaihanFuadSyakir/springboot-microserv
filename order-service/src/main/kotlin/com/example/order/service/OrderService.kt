package com.example.order.service

import com.example.order.model.Order
import com.example.order.model.OrderStatus
import com.example.order.repository.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository
) {
    fun createOrder(order: Order): Order {
        // Set initial status to PENDING
        val orderToSave = order.copy(status = OrderStatus.PENDING)
        return orderRepository.save(orderToSave)
    }
    
    fun getOrderById(id: Long): Optional<Order> {
        return orderRepository.findById(id)
    }
    
    fun getAllOrders(): List<Order> {
        return orderRepository.findAll()
    }
    
    fun updateOrder(id: Long, order: Order): Optional<Order> {
        return if (orderRepository.existsById(id)) {
            val orderToUpdate = order.copy(id = id, updatedAt = java.time.LocalDateTime.now())
            Optional.of(orderRepository.save(orderToUpdate))
        } else {
            Optional.empty()
        }
    }
    
    fun deleteOrder(id: Long): Boolean {
        return if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id)
            true
        } else {
            false
        }
    }
    
    fun getOrdersByUserId(userId: Long): List<Order> {
        return orderRepository.findByUserId(userId)
    }
    
    fun getOrdersByStatus(status: OrderStatus): List<Order> {
        return orderRepository.findByStatus(status)
    }
    
    fun getOrdersByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order> {
        return orderRepository.findByUserIdAndStatus(userId, status)
    }
    
    fun updateOrderStatus(id: Long, status: OrderStatus): Optional<Order> {
        val orderOptional = orderRepository.findById(id)
        return if (orderOptional.isPresent) {
            val order = orderOptional.get()
            val updatedOrder = order.copy(status = status, updatedAt = java.time.LocalDateTime.now())
            Optional.of(orderRepository.save(updatedOrder))
        } else {
            Optional.empty()
        }
    }
}