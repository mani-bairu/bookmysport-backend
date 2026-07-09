package com.bookmysport.backend.booking.service;

import com.bookmysport.backend.booking.dto.BookingResponseDto;
import com.bookmysport.backend.booking.entity.BookingEntity;
import com.bookmysport.backend.booking.mapper.BookingMapper;
import com.bookmysport.backend.booking.repository.BookingRepository;
import com.bookmysport.backend.common.enums.BookingStatus;
import com.bookmysport.backend.exception.BadRequestException;
import com.bookmysport.backend.exception.BookingException;
import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.slot.entity.SlotStatus;
import com.bookmysport.backend.slot.respository.SlotRepository;
import com.bookmysport.backend.websocket.dto.SlotEvent;
import com.bookmysport.backend.websocket.service.SlotWebSocketService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BookingService {

    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final StringRedisTemplate redisTemplate;
    private final SlotWebSocketService webSocketService;
    private final BookingMapper bookingMapper;

    public BookingResponseDto createBooking(Long slotId, Long userId) {

       SlotEntity slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // 1. Must be LOCKED
        if (slot.getStatus() != SlotStatus.LOCKED) {
            throw new BadRequestException("Slot not locked");
        }

        // 2. Must be locked by same user
        if (!userId.equals(slot.getLockedByUser())) {
            throw new BadRequestException("Slot locked by another user");
        }

        // 3. Check Redis lock still exists
        String key = "lock:" + slotId;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            throw new BadRequestException("Lock expired");
        }

//        BookingEntity existingBooking =
//                bookingRepository.findBySlotIdAndStatus(
//                        slotId,
//                        BookingStatus.PENDING_PAYMENT);
//
//        if (existingBooking != null) {
//            throw new BookingException("Booking already in progress.");
//        }

        // 4. Create booking
        BookingEntity booking = BookingEntity.builder()
                .slotId(slotId)
                .userId(userId)
                .status(BookingStatus.PENDING_PAYMENT)
                .amount(slot.getPrice())
                .bookedDate(slot.getDate())
                .slotStartTime(slot.getStartTime())
                .slotEndTime(slot.getEndTime())
                .build();

        bookingRepository.save(booking);

        BookingResponseDto bookingResponseDto = bookingMapper.entityToResponseDto(booking);


        // 5. Notify frontend
        webSocketService.sendSlotEvent(
                new SlotEvent(slotId, "BOOKING_CREATED", userId)
        );

        return bookingResponseDto;
    }









    @Transactional
    public BookingEntity confirmBooking(Long bookingId) {

        BookingEntity booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(
                                () -> new ResourseNotFoundException(
                                        "Booking not found"
                                )
                        );


        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {

            throw new BadRequestException(
                    "Booking is not waiting for payment"
            );
        }


        booking.setStatus(
                BookingStatus.CONFIRMED
        );


        return bookingRepository.save(booking);
    }
}
