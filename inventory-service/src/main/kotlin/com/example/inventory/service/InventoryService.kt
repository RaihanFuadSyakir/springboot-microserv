package com.example.inventory.service

import com.example.inventory.model.Inventory
import com.example.inventory.repository.InventoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class InventoryService(
    private val inventoryRepository: InventoryRepository
) {
    fun createInventory(inventory: Inventory): Inventory {
        // Check if inventory for this product already exists
        if (inventoryRepository.existsByProductId(inventory.productId)) {
            throw RuntimeException("Inventory for product ${inventory.productId} already exists")
        }
        return inventoryRepository.save(inventory)
    }
    
    fun getInventoryById(id: Long): Optional<Inventory> {
        return inventoryRepository.findById(id)
    }
    
    fun getInventoryByProductId(productId: Long): Optional<Inventory> {
        return inventoryRepository.findByProductId(productId)
    }
    
    fun getAllInventory(): List<Inventory> {
        return inventoryRepository.findAll()
    }
    
    fun updateInventory(id: Long, inventory: Inventory): Optional<Inventory> {
        return if (inventoryRepository.existsById(id)) {
            val inventoryToUpdate = inventory.copy(id = id, updatedAt = java.time.LocalDateTime.now())
            Optional.of(inventoryRepository.save(inventoryToUpdate))
        } else {
            Optional.empty()
        }
    }
    
    fun deleteInventory(id: Long): Boolean {
        return if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id)
            true
        } else {
            false
        }
    }
    
    fun updateQuantity(productId: Long, quantity: Int): Optional<Inventory> {
        val inventoryOptional = inventoryRepository.findByProductId(productId)
        return if (inventoryOptional.isPresent) {
            val inventory = inventoryOptional.get()
            val updatedInventory = inventory.copy(
                quantity = quantity, 
                updatedAt = java.time.LocalDateTime.now()
            )
            Optional.of(inventoryRepository.save(updatedInventory))
        } else {
            Optional.empty()
        }
    }
    
    fun reserveStock(productId: Long, quantity: Int): Boolean {
        val inventoryOptional = inventoryRepository.findByProductId(productId)
        return if (inventoryOptional.isPresent) {
            val inventory = inventoryOptional.get()
            if (inventory.availableQuantity() >= quantity) {
                val updatedInventory = inventory.copy(
                    reservedQuantity = inventory.reservedQuantity + quantity,
                    updatedAt = java.time.LocalDateTime.now()
                )
                inventoryRepository.save(updatedInventory)
                true
            } else {
                false
            }
        } else {
            false
        }
    }
    
    fun releaseReservedStock(productId: Long, quantity: Int): Boolean {
        val inventoryOptional = inventoryRepository.findByProductId(productId)
        return if (inventoryOptional.isPresent) {
            val inventory = inventoryOptional.get()
            if (inventory.reservedQuantity >= quantity) {
                val updatedInventory = inventory.copy(
                    reservedQuantity = Math.max(0, inventory.reservedQuantity - quantity),
                    updatedAt = java.time.LocalDateTime.now()
                )
                inventoryRepository.save(updatedInventory)
                true
            } else {
                false
            }
        } else {
            false
        }
    }
    
    fun commitStock(productId: Long, quantity: Int): Boolean {
        val inventoryOptional = inventoryRepository.findByProductId(productId)
        return if (inventoryOptional.isPresent) {
            val inventory = inventoryOptional.get()
            if (inventory.reservedQuantity >= quantity) {
                val updatedInventory = inventory.copy(
                    quantity = inventory.quantity - quantity,
                    reservedQuantity = inventory.reservedQuantity - quantity,
                    updatedAt = java.time.LocalDateTime.now()
                )
                inventoryRepository.save(updatedInventory)
                true
            } else {
                false
            }
        } else {
            false
        }
    }
}