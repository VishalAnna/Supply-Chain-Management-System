package com.company.scm.service;

import com.company.scm.exception.EntityNotFoundException;
import com.company.scm.exception.InsufficientStockException;
import com.company.scm.exception.InvalidQuantityException;
import com.company.scm.model.PurchaseOrder;
import com.company.scm.model.SalesOrder;
import com.company.scm.model.Shipment;
import com.company.scm.repository.PurchaseOrderRepository;
import com.company.scm.repository.SalesOrderRepository;
import com.company.scm.repository.ShipmentRepository;

import java.util.UUID;

/**
 * Manages the lifecycle of sales and purchase orders.
 */
public class OrderService {
    private final InventoryService inventoryService;
    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ShipmentRepository shipmentRepository;

    public OrderService(InventoryService inventoryService, SalesOrderRepository salesOrderRepository,
                        PurchaseOrderRepository purchaseOrderRepository, ShipmentRepository shipmentRepository) {
        this.inventoryService = inventoryService;
        this.salesOrderRepository = salesOrderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.shipmentRepository = shipmentRepository;
    }

    /**
     * Creates a new sales order after checking for sufficient stock.
     */
    public SalesOrder createSalesOrder(String productId, int quantity, String customerDetails, String warehouseId)
            throws InsufficientStockException, InvalidQuantityException { //
        if (quantity <= 0) {
            throw new InvalidQuantityException("Order quantity must be positive.");
        }
        if (inventoryService.getStockLevel(productId, warehouseId) < quantity) {
            throw new InsufficientStockException("Not enough stock to create sales order.");
        }

        String orderId = "SO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SalesOrder order = new SalesOrder(orderId, productId, quantity, customerDetails);
        salesOrderRepository.save(order);
        return order;
    }

    /**
     * Fulfills a sales order, which deducts stock and creates a shipment.
     */
    public Shipment fulfillSalesOrder(String orderId, String warehouseId, String destination)
            throws EntityNotFoundException, InsufficientStockException, InvalidQuantityException { //
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Sales Order not found: " + orderId));

        if (order.getStatus() == SalesOrder.Status.FULFILLED) {
            throw new IllegalStateException("Order " + orderId + " has already been fulfilled.");
        }

        // Deduct stock from inventory
        inventoryService.removeStock(order.getProductId(), warehouseId, order.getQuantity());
        order.setStatus(SalesOrder.Status.FULFILLED);
        salesOrderRepository.save(order);

        // Create shipment record
        String shipmentId = "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Shipment shipment = new Shipment(shipmentId, orderId, warehouseId, destination);
        shipmentRepository.save(shipment);

        return shipment;
    }
    
    /**
     * Creates a new purchase order to replenish stock.
     */
    public PurchaseOrder createPurchaseOrder(String supplierId, String productId, int quantity)
            throws InvalidQuantityException { //
        if (quantity <= 0) {
            throw new InvalidQuantityException("Purchase quantity must be positive.");
        }
        String orderId = "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PurchaseOrder po = new PurchaseOrder(orderId, supplierId, productId, quantity);
        purchaseOrderRepository.save(po);
        return po;
    }
}