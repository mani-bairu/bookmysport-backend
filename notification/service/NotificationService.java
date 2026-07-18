package com.bookmysport.backend.notification.service;

import com.bookmysport.backend.booking.dto.BookingDetailsDto;
import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.slot.respository.SlotRepository;
import com.bookmysport.backend.user.entity.UserEntity;
import com.bookmysport.backend.user.repository.UserRepository;
import com.bookmysport.backend.venue.entity.VenueEntity;
import com.bookmysport.backend.venue.repository.VenueRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final SlotRepository slotRepository;

    private final VenueRepository venueRepository;

    private final ResourceLoader resourceLoader;


    private String loadTemplate(String fileName) throws IOException {

        Resource resource = resourceLoader.getResource(
                "classpath:template/" + fileName
        );

        try (InputStream inputStream = resource.getInputStream()) {

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

    public void sendBookingConfirmation(
            BookingDetailsDto booking
    ) throws IOException, MessagingException {

        // 1. Load HTML template
        String template = loadTemplate("booking-confirmation.html");


//        // 1. Load HTML template
//        String template = Files.readString(Path.of(path+ "booking-confirmation.html"));


        // 2. Replace dynamic values

        String emailContent = template
                .replace("{{userName}}", booking.getUserName())
                .replace("{{venueName}}", booking.getVenueName())
                .replace("{{sportName}}", booking.getSportAreaName())
                .replace("{{date}}", String.valueOf(booking.getBookedDate()))
                .replace("{{startTime}}", formatTime(booking.getSlotStartTime()))
                .replace("{{endTime}}", formatTime(booking.getSlotEndTime()))
                .replace("{{amount}}", String.valueOf(booking.getSlotPrice()))
                .replace("{{bookingId}}",
                        String.valueOf(booking.getBookedUserId())
                                    +booking.getBookedSlotId()
                                    +booking.getBookingId()
                        );




        // 3. Send email

        emailService.sendHtmlEmail(
                booking.getUserEmail(),
                "Booking Confirmed - BookMySport",
                emailContent
        );
    }

    public void sendWelcomeEmail(
            UserEntity user
    ) throws IOException, MessagingException {


        // 1. Load HTML template
        String template = loadTemplate("welcome-email.html");

        String content =
                template.replace(
                        "{{userName}}",
                        user.getName()
                );


        emailService.sendHtmlEmail(
                user.getEmail(),
                "Welcome to BookMySport 🎉",
                content
        );
    }

    public void sendOtpEmail(String email, String otp) throws IOException, MessagingException {

        try {

            // 1. Load HTML template
            String template = loadTemplate("otp-email.html");

            String content = template.replace("{{otp}}", otp);

            emailService.sendHtmlEmail(email,"Verify your BookMySport account", content);

            System.out.println("Otp sent to user Email");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPasswordResetEmail(String email, String name, String resetLink)
            throws MessagingException, IOException {

        String template = loadTemplate("password-reset-email.html");
        String content = template
                .replace("{{userName}}", name)
                .replace("{{resetLink}}", resetLink);

        emailService.sendHtmlEmail(email, "Reset your BookMySport password", content);
        log.info("Password reset email sent to:{}", email);
    }





    private String formatTime(LocalTime time) {
        if (time == null) return "—";
        int hour = time.getHour();
        String ampm = hour >= 12 ? "PM" : "AM";
        int hour12 = hour % 12;
        if (hour12 == 0) hour12 = 12;
        return hour12 + " " + ampm;
    }






}
