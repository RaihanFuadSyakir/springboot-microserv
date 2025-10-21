package com.example.inventory.repository

import com.example.inventory.model.Inventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface InventoryRepository : JpaRepository<Inventory, Long> {
    fun findByProductId(productId: Long): Optional<Inventory>
    fun existsByProductId(productId: Long): Boolean
}