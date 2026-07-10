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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final SlotRepository slotRepository;

    private final VenueRepository venueRepository;

    String path = "src/main/java/com/bookmysport/backend/notification/template/";



    public void sendBookingConfirmation(
            BookingDetailsDto booking
    ) throws IOException, MessagingException {

//        String path = "src/main/java/com/bookmysport/backend/notification/template/";

//        UserEntity user= userRepository.findById(booking.getUserId())
//                .orElseThrow(()-> new ResourseNotFoundException("user not found"));
//
//        SlotEntity slot = slotRepository.findById(booking.getSlotId()).
//                orElseThrow(()-> new ResourseNotFoundException("slot not found"));
//
//
//        VenueEntity venue = venueRepository.findById(slot.getVenueId())
//                .orElseThrow(() -> new ResourseNotFoundException("Venue Not Found!"));


        // 1. Load HTML template
        String template = Files.readString(Path.of(path+ "booking-confirmation.html"));


        // 2. Replace dynamic values

        String emailContent = template.replace("{{userName}}", booking.getUserName())
                        .replace(
                                "{{venueName}}",
                                booking.getVenueName()
                        )
                        .replace(
                                "{{sportName}}",
                                booking.getSportAreaName()

                        ).replace(
                        "{{date}}",
                        String.valueOf(booking.getBookedDate())
                        )




                        .replace(
                                "{{startTime}}",
                                String.valueOf(booking.getSlotStartTime())
                        )
                        .replace(
                                "{{endTime}}",
                                String.valueOf(booking.getSlotEndTime())
                        )
                        .replace(
                                "{{amount}}",
                                String.valueOf(booking.getSlotPrice())
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


        String template =
                Files.readString(
                        Path.of(
                                path + "welcome-email.html"
                        )
                );


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

            String template = Files.readString(Path.of(path + "otp-email.html"));

            String content = template.replace("{{otp}}", otp);

            emailService.sendHtmlEmail(email,"Verify your BookMySport account", content);

            System.out.println("Otp sent to user Email");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
