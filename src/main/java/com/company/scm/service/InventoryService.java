package com.company.scm.service;

import com.company.scm.exception.EntityNotFoundException;
import com.company.scm.exception.InsufficientStockException;
import com.company.scm.exception.InvalidQuantityException;
import com.company.scm.model.InventoryItem;
import com.company.scm.repository.InventoryRepository;

/**
 * Manages core inventory operations like adding, removing, and transferring stock.
 */
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Adds a specified quantity of a product to a warehouse.
     */
    public void addStock(String productId, String warehouseId, int quantity) throws InvalidQuantityException { //
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity to add must be positive.");
        }

        InventoryItem item = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElse(new InventoryItem(productId, warehouseId, 0));

        item.setQuantity(item.getQuantity() + quantity);
        inventoryRepository.save(item);
    }

    /**
     * Removes a specified quantity of a product from a warehouse.
     */
    public void removeStock(String productId, String warehouseId, int quantity)
            throws InsufficientStockException, InvalidQuantityException, EntityNotFoundException { //
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity to remove must be positive.");
        }

        InventoryItem item = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory item not found for product: " + productId));

        if (item.getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product " + productId + ". Available: " + item.getQuantity() + ", Required: " + quantity); //
        }

        item.setQuantity(item.getQuantity() - quantity);
        inventoryRepository.save(item);
    }

    /**
     * Gets the current stock level for a product in a specific warehouse.
     */
    public int getStockLevel(String productId, String warehouseId) { //
        return inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .map(InventoryItem::getQuantity)
                .orElse(0);
    }

    /**
     * Transfers stock from one warehouse to another.
     */
    public void transferStock(String productId, String fromWarehouseId, String toWarehouseId, int quantity)
            throws InsufficientStockException, InvalidQuantityException, EntityNotFoundException { //
        if (fromWarehouseId.equals(toWarehouseId)) {
            throw new IllegalArgumentException("Source and destination warehouses cannot be the same.");
        }
        
        // Atomically remove from source and add to destination
        removeStock(productId, fromWarehouseId, quantity);
        addStock(productId, toWarehouseId, quantity);
    }
}