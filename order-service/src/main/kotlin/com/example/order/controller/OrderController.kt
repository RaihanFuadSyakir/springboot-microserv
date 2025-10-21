package com.example.order.controller

import com.example.order.model.Order
import com.example.order.model.OrderStatus
import com.example.order.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    
    @PostMapping
    fun createOrder(@RequestBody order: Order): ResponseEntity<Order> {
        val createdOrder = orderService.createOrder(order)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder)
    }
    
    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<Order> {
        val order = orderService.getOrderById(id)
        return if (order.isPresent) {
            ResponseEntity.ok(order.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping
    fun getAllOrders(): ResponseEntity<List<Order>> {
        val orders = orderService.getAllOrders()
        return ResponseEntity.ok(orders)
    }
    
    @PutMapping("/{id}")
    fun updateOrder(@PathVariable id: Long, @RequestBody order: Order): ResponseEntity<Order> {
        val updatedOrder = orderService.updateOrder(id, order)
        return if (updatedOrder.isPresent) {
            ResponseEntity.ok(updatedOrder.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = orderService.deleteOrder(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/user/{userId}")
    fun getOrdersByUserId(@PathVariable userId: Long): ResponseEntity<List<Order>> {
        val orders = orderService.getOrdersByUserId(userId)
        return ResponseEntity.ok(orders)
    }
    
    @GetMapping("/status/{status}")
    fun getOrdersByStatus(@PathVariable status: OrderStatus): ResponseEntity<List<Order>> {
        val orders = orderService.getOrdersByStatus(status)
        return ResponseEntity.ok(orders)
    }
    
    @PutMapping("/{id}/status")
    fun updateOrderStatus(@PathVariable id: Long, @RequestBody status: OrderStatus): ResponseEntity<Order> {
        val updatedOrder = orderService.updateOrderStatus(id, status)
        return if (updatedOrder.isPresent) {
            ResponseEntity.ok(updatedOrder.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
}