// package com.destination.backend.service;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.scheduling.annotation.EnableAsync;
// import org.springframework.stereotype.Service;

// import com.destination.backend.entity.Admin;
// import com.destination.backend.entity.Order;
// import com.destination.backend.entity.OrderItem;
// import com.destination.backend.repository.AdminRepository;

// import jakarta.mail.internet.MimeMessage;

// @Service
// @EnableAsync
// public class EmailService {

//         @Autowired
//         private JavaMailSender mailSender;

//         @Autowired
//         private AdminRepository adminRepository;

//         @Value("${spring.mail.username}")
//         private String fromEmail;

//         @Async
//         public void sendTempPassword(
//                         String toEmail,
//                         String tempPassword,
//                         String username) {

//                 try {

//                         // ✅ Create Mime Message
//                         MimeMessage message = mailSender.createMimeMessage();

//                         MimeMessageHelper helper = new MimeMessageHelper(message, true);

//                         // ✅ IMPORTANT FIX
//                         helper.setFrom(fromEmail);
//                         helper.setTo(toEmail);

//                         // ✅ Subject
//                         helper.setSubject(
//                                         "Password Reset - Destination App");

//                         // ✅ HTML Email Content
//                         String content = "<p>Dear <b>" + username + "</b>,</p>"

//                                         + "<p>We received a request to reset your "
//                                         + "password for the Destination App.</p>"

//                                         + "<p>Your temporary password is: "
//                                         + "<b>" + tempPassword + "</b></p>"

//                                         + "<p style='color:red;'>"
//                                         + "⚠️ This is a temporary password. "
//                                         + "Please login using this password and "
//                                         + "update your password immediately."
//                                         + "</p>"

//                                         + "<p>If you did not request this, "
//                                         + "please ignore this email.</p>"

//                                         + "<br>"

//                                         + "<p>Thanks & Regards,<br>"
//                                         + "Destination Team</p>";

//                         // ✅ Enable HTML
//                         helper.setText(content, true);

//                         // ✅ Send Mail
//                         mailSender.send(message);

//                         System.out.println(
//                                         "✅ Password reset email sent successfully");

//                 } catch (Exception e) {

//                         System.out.println(
//                                         "========== EMAIL ERROR ==========");

//                         e.printStackTrace();

//                         System.out.println(
//                                         "MESSAGE: " + e.getMessage());

//                         System.out.println(
//                                         "CAUSE: " + e.getCause());

//                         System.out.println(
//                                         "================================");
//                 }
//         }

//         public void sendOrderEmail(Order order) {

//                 System.out.println("EMAIL METHOD STARTED");

//                 try {

//                         StringBuilder text = new StringBuilder();

//                         text.append("New Order Received\n\n");
//                         text.append("Customer Name: ")
//                                         .append(order.getUserDetails().getName())
//                                         .append("\n");

//                         text.append("Mobile: ")
//                                         .append(order.getUserDetails().getMobileNumber())
//                                         .append("\n\n");

//                         text.append("Products:\n");

//                         for (OrderItem item : order.getItems()) {

//                                 text.append(item.getProductName())
//                                                 .append(" x ")
//                                                 .append(item.getQuantity())
//                                                 .append("\n");
//                         }

//                         text.append("\nTotal Price: ")
//                                         .append(order.getTotalPrice());

//                         List<Admin> admins = adminRepository.findAll();

//                         System.out.println("ADMINS COUNT: " + admins.size());

//                         for (Admin admin : admins) {

//                                 try {

//                                         if (admin.getEmail() != null &&
//                                                         !admin.getEmail().isBlank()) {

//                                                 System.out.println("SENDING TO: " + admin.getEmail());

//                                                 SimpleMailMessage message = new SimpleMailMessage();

//                                                 message.setFrom(fromEmail);
//                                                 message.setTo(admin.getEmail());
//                                                 message.setSubject("New Order Received");
//                                                 message.setText(text.toString());

//                                                 mailSender.send(message);

//                                                 System.out.println("MAIL SENT SUCCESS");
//                                         }

//                                 } catch (Exception e) {
//                                         System.out.println("FAILED FOR: " + admin.getEmail());
//                                         e.printStackTrace();
//                                 }
//                         }

//                 } catch (Exception e) {
//                         System.out.println("MAIN EMAIL ERROR");
//                         e.printStackTrace();
//                 }
//         }

//         @Async
//         public void sendDeliveryEmail(Order order) {

//                 try {

//                         String userName = order.getUserDetails().getName();
//                         String email = order.getUserDetails().getEmail();

//                         StringBuilder text = new StringBuilder();

//                         text.append("Dear ")
//                                         .append(userName)
//                                         .append(",\n\n");

//                         text.append("Thank you for your order.\n\n");

//                         text.append("Your order has been successfully delivered.\n\n");

//                         text.append("Order Details:\n");

//                         double totalAmount = 0;

//                         for (OrderItem item : order.getItems()) {

//                                 text.append("- ")
//                                                 .append(item.getProductName())
//                                                 .append(" x ")
//                                                 .append(item.getQuantity())
//                                                 .append(" = ₹")
//                                                 .append(item.getTotalPrice())
//                                                 .append("\n");

//                                 totalAmount += item.getTotalPrice();
//                         }

//                         text.append("\nTotal Amount: ₹")
//                                         .append(totalAmount);

//                         text.append("\n\nWe hope you enjoyed your order 😋\n");

//                         text.append("Thank you for choosing Destination!\n\n");

//                         text.append("Best Regards,\n");
//                         text.append("Destination Team");

//                         // 📧 Send Email
//                         SimpleMailMessage message = new SimpleMailMessage();
//                         message.setFrom(fromEmail);
//                         message.setTo(email);

//                         message.setSubject(
//                                         "Your Order Has Been Delivered 🎉");

//                         message.setText(text.toString());

//                         mailSender.send(message);

//                 } catch (Exception e) {

//                         System.out.println(
//                                         "Delivery email failed: "
//                                                         + e.getMessage());
//                 }
//         }
// }

package com.destination.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.destination.backend.entity.Admin;
import com.destination.backend.entity.Order;
import com.destination.backend.entity.OrderItem;
import com.destination.backend.repository.AdminRepository;

@Service
public class EmailService {

        private final AdminRepository adminRepository;

        private final RestTemplate restTemplate = new RestTemplate();

        @Value("${resend.api.key : re_Gx6aJEcD_MD9GZg4deweTYuMdBefW2BAP}")
        private String apiKey;

        public EmailService(AdminRepository adminRepository) {
                this.adminRepository = adminRepository;
        }

        // ========================
        // 1. TEMP PASSWORD EMAIL
        // ========================
        @Async
        public void sendTempPassword(String toEmail, String tempPassword, String username) {

                String subject = "Password Reset - Destination App";

                String content = "Dear " + username + ",\n\n" +
                                "Your temporary password is: " + tempPassword + "\n\n" +
                                "Please change it after login.\n\n" +
                                "Destination Team";

                sendEmail(toEmail, subject, content);
        }

        // ========================
        // 2. ORDER EMAIL TO ADMINS
        // ========================
        @Async
        public void sendOrderEmail(Order order) {

                System.out.println("EMAIL METHOD STARTED");

                StringBuilder text = new StringBuilder();

                text.append("New Order Received\n\n");
                text.append("Customer Name: ")
                                .append(order.getUserDetails().getName())
                                .append("\n");

                text.append("Mobile: ")
                                .append(order.getUserDetails().getMobileNumber())
                                .append("\n\n");

                text.append("Products:\n");

                for (OrderItem item : order.getItems()) {
                        text.append(item.getProductName())
                                        .append(" x ")
                                        .append(item.getQuantity())
                                        .append("\n");
                }

                text.append("\nTotal Price: ")
                                .append(order.getTotalPrice());

                List<Admin> admins = adminRepository.findAll();

                for (Admin admin : admins) {

                        if (admin.getEmail() != null && !admin.getEmail().isBlank()) {
                                System.out.println("SENDING TO: " + admin.getEmail());

                                sendEmail(
                                                admin.getEmail(),
                                                "New Order Received",
                                                text.toString());
                        }
                }
        }

        // ========================
        // 3. DELIVERY EMAIL
        // ========================
        @Async
        public void sendDeliveryEmail(Order order) {

                String userName = order.getUserDetails().getName();
                String email = order.getUserDetails().getEmail();

                StringBuilder text = new StringBuilder();

                text.append("Dear ").append(userName).append(",\n\n");
                text.append("Your order has been delivered.\n\n");

                double total = 0;

                for (OrderItem item : order.getItems()) {
                        text.append(item.getProductName())
                                        .append(" x ")
                                        .append(item.getQuantity())
                                        .append(" = ₹")
                                        .append(item.getTotalPrice())
                                        .append("\n");

                        total += item.getTotalPrice();
                }

                text.append("\nTotal: ₹").append(total);

                sendEmail(email, "Order Delivered 🎉", text.toString());
        }

        // ========================
        // COMMON EMAIL METHOD (API)
        // ========================
        private void sendEmail(String to, String subject, String text) {

                String url = "https://api.resend.com/emails";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);
                // ✅ Add professional footer with contact email
                String finalText = text +
                                "\n\n----------------------------\n" +
                                "Need help or have questions?\n" +
                                "Contact: destination56662025@gmail.com\n" +
                                "We usually respond within 24 hours.\n" +
                                "----------------------------";

                Map<String, Object> body = new HashMap<>();
                body.put("from", "No Reply - Destination <onboarding@resend.dev>"); // default resend domain
                body.put("to", to);
                body.put("subject", subject);
                body.put("text", finalText);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

                try {
                        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                        System.out.println("EMAIL SENT: " + response.getBody());

                } catch (Exception e) {
                        System.out.println("EMAIL FAILED: " + e.getMessage());
                }
        }
}
