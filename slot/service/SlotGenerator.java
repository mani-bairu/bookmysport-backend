package com.bookmysport.backend.slot.service;

import com.bookmysport.backend.slot.entity.SlotEntity;
import com.bookmysport.backend.slot.entity.SlotStatus;
import com.bookmysport.backend.slot.respository.SlotRepository;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SlotGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    private final SlotRepository slotRepository;

    private final PriceResolverService pricingResolverService;

    @Transactional
    public void generate(SportAreaEntity sportArea, LocalDate startDate, LocalDate endDate){


        System.out.println("--------------------------------");
        System.out.println("Generating Area : " + sportArea.getId());
        System.out.println("Start Date      : " + startDate);
        System.out.println("End Date        : " + endDate);
        System.out.println("--------------------------------");

        List<SlotEntity> slots = new ArrayList<>();
        int BATCH_SIZE = 50;

        for(LocalDate date=startDate; !date.isAfter(endDate); date = date.plusDays(1)){

            LocalTime openTime = sportArea.getOpeningTime();
            LocalTime closeTime = sportArea.getClosingTime();
            Integer duration = sportArea.getDurationMinutes();

            LocalDateTime current =
                    LocalDateTime.of(date, openTime);

            LocalDateTime closing =
                    LocalDateTime.of(date, closeTime);

            System.out.println(
                    sportArea.getId() + " "
                            + date + " "
                            + openTime
            );

            while (true) {

                LocalDateTime end = current.plusMinutes(duration);

                if (end.isAfter(closing)) {
                    break;
                }

                BigDecimal price =
                        pricingResolverService.resolvePrice(
                                sportArea,
                                date,
                                current.toLocalTime()
                        );

                SlotEntity slot = SlotEntity.builder()
                        .venueId(sportArea.getVenue().getId())
                        .sportAreaId(sportArea.getId())
                        .date(date)
                        .startTime(current.toLocalTime())
                        .endTime(end.toLocalTime())
                        .status(SlotStatus.AVAILABLE)
                        .price(price)
                        .build();

                slots.add(slot);

                if (slots.size() == BATCH_SIZE) {
                    slotRepository.saveAll(slots);
                    slotRepository.flush();   // IMPORTANT
                    entityManager.clear();      // 💥 frees memory
                    slots.clear();
                }
                current=end;


            }
        }
        if (!slots.isEmpty()) {
            slotRepository.saveAll(slots);
            slotRepository.flush();
            entityManager.clear();
        }
//        slotRepository.saveAll(slots);
    }
}
