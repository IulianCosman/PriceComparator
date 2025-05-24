package priceComparator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import priceComparator.models.PriceAlert;
import priceComparator.services.PriceAlertService;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller exposing endpoints for managing price alerts.
 */
@RestController
@RequestMapping("/alerts")
public class PriceAlertController {

    @Autowired
    private PriceAlertService alertService;


    /**
     * Endpoint to create a new price alert for a product.
     *
     * @param email the email of the person who created the alert
     * @param productName the name of the product the person wants
     * @param targetPrice the price that the product should drop to
     * @return the saved alert
     */
    @PostMapping
    public ResponseEntity<PriceAlert> createAlert(@RequestParam String email, @RequestParam String productName, Double targetPrice) {
        PriceAlert alert = new PriceAlert();
        alert.setCreatedAt(LocalDate.now());
        alert.setProductName(productName);
        alert.setNotified(false);
        alert.setUserEmail(email);
        alert.setTargetPrice(targetPrice);
        return ResponseEntity.ok(alertService.createAlert(alert));
    }

    /**
     * Endpoint to manually trigger a check for all unnotified alerts.
     * This method will:
     * - Retrieve all alerts that haven't triggered yet (notified = false)
     * - Evaluate the current best price for each product
     * - If the current price is <= the target, mark alert as notified
     *
     * @return List of triggered alerts
     */
    @GetMapping("/check")
    public ResponseEntity<List<PriceAlert>> checkAlerts() {
        List<PriceAlert> triggered = alertService.checkAndTriggerAlerts();
        return ResponseEntity.ok(triggered);
    }
}