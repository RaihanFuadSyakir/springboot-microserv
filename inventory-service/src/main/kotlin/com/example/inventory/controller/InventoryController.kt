package com.example.inventory.controller

import com.example.inventory.model.Inventory
import com.example.inventory.service.InventoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/inventory")
class InventoryController(
    private val inventoryService: InventoryService
) {
    
    @PostMapping
    fun createInventory(@RequestBody inventory: Inventory): ResponseEntity<Inventory> {
        return try {
            val createdInventory = inventoryService.createInventory(inventory)
            ResponseEntity.status(HttpStatus.CREATED).body(createdInventory)
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
    
    @GetMapping("/{id}")
    fun getInventoryById(@PathVariable id: Long): ResponseEntity<Inventory> {
        val inventory = inventoryService.getInventoryById(id)
        return if (inventory.isPresent) {
            ResponseEntity.ok(inventory.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/product/{productId}")
    fun getInventoryByProductId(@PathVariable productId: Long): ResponseEntity<Inventory> {
        val inventory = inventoryService.getInventoryByProductId(productId)
        return if (inventory.isPresent) {
            ResponseEntity.ok(inventory.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping
    fun getAllInventory(): ResponseEntity<List<Inventory>> {
        val inventoryList = inventoryService.getAllInventory()
        return ResponseEntity.ok(inventoryList)
    }
    
    @PutMapping("/{id}")
    fun updateInventory(@PathVariable id: Long, @RequestBody inventory: Inventory): ResponseEntity<Inventory> {
        val updatedInventory = inventoryService.updateInventory(id, inventory)
        return if (updatedInventory.isPresent) {
            ResponseEntity.ok(updatedInventory.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteInventory(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = inventoryService.deleteInventory(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PutMapping("/product/{productId}/quantity")
    fun updateQuantity(@PathVariable productId: Long, @RequestParam quantity: Int): ResponseEntity<Inventory> {
        val updatedInventory = inventoryService.updateQuantity(productId, quantity)
        return if (updatedInventory.isPresent) {
            ResponseEntity.ok(updatedInventory.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping("/product/{productId}/reserve")
    fun reserveStock(@PathVariable productId: Long, @RequestParam quantity: Int): ResponseEntity<Boolean> {
        val success = inventoryService.reserveStock(productId, quantity)
        return if (success) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body(false)
        }
    }
    
    @PostMapping("/product/{productId}/release")
    fun releaseReservedStock(@PathVariable productId: Long, @RequestParam quantity: Int): ResponseEntity<Boolean> {
        val success = inventoryService.releaseReservedStock(productId, quantity)
        return ResponseEntity.ok(success)
    }
    
    @PostMapping("/product/{productId}/commit")
    fun commitStock(@PathVariable productId: Long, @RequestParam quantity: Int): ResponseEntity<Boolean> {
        val success = inventoryService.commitStock(productId, quantity)
        return if (success) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body(false)
        }
    }
}