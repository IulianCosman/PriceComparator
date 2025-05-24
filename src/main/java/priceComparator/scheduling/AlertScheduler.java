package priceComparator.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import priceComparator.services.PriceAlertService;

/**
 * Scheduler for running price alert checks on a fixed schedule.
 */
@Component
public class AlertScheduler {

    @Autowired
    private PriceAlertService alertService;

    /**
     * Runs the alert checker every day at 8 AM.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void runAlertChecker() {
        alertService.checkAndTriggerAlerts();
    }
}
