package com.destination.backend.scheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepAliveScheduler {

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void keepAlive() {

        try {

            String response = restTemplate.getForObject(
                    "https://destination-backend-hr7f.onrender.com/api/products/getAll",
                    String.class
            );

            System.out.println("Scheduler Executed Successfully");

        } catch (Exception e) {
            System.out.println("Error while calling API");
            e.printStackTrace();
        }
    }
}
