package priceComparator.services;

import priceComparator.dtos.ProductDTO;
import priceComparator.models.PriceAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priceComparator.repositories.PriceAlertRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service handling business logic related to {@link PriceAlert}.
 * Responsible for creating, managing, and triggering price alerts.
 */
@Service
public class PriceAlertService {

    @Autowired
    private PriceAlertRepository alertRepo;

    @Autowired
    private PriceEvaluatorService priceEvaluatorService;

    @Autowired(required = false)
    private EmailNotificationService emailNotificationService;

    /**
     * Checks all {@link PriceAlert} entries that have not been notified.
     * If the current price (with any active discount) is less than or equal to the user's target price,
     * the alert is marked as notified and an email is sent.
     * @return a list of the {@link PriceAlert} entries that were triggered.
     */
    public List<PriceAlert> checkAndTriggerAlerts() {
        List<PriceAlert> alertsToCheck = alertRepo.findByNotifiedFalse();
        List<PriceAlert> triggered = new ArrayList<>();

        for (PriceAlert alert : alertsToCheck) {
            Optional<ProductDTO> bestPrice = priceEvaluatorService.getProductWithLowestPrice(alert.getProductName());

            if (bestPrice.isPresent() && bestPrice.get().getDiscountedPrice() <= alert.getTargetPrice()) {
                alert.setNotified(true);
                triggered.add(alert);

                // Send email if service is available
                if (emailNotificationService != null) {
                    emailNotificationService.sendPriceReachedEmail(
                            alert.getUserEmail(),
                            alert.getProductName(),
                            alert.getCreatedAt(),
                            bestPrice.get().getDiscountedPrice()
                    );
                }
            }
        }

        // Persist state changes (marking alerts as notified)
        alertRepo.saveAll(triggered);
        return triggered;
    }

    /**
     * Creates and saves a new price alert with default values.
     *
     * @param alert the alert to be saved
     * @return the saved alert
     */
    public PriceAlert createAlert(PriceAlert alert) {
        alert.setCreatedAt(LocalDate.now());
        alert.setNotified(false);
        return alertRepo.save(alert);
    }

}

