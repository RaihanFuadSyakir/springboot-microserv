package com.example.order.repository

import com.example.order.model.Order
import com.example.order.model.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByUserId(userId: Long): List<Order>
    fun findByStatus(status: OrderStatus): List<Order>
    fun findByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order>
}