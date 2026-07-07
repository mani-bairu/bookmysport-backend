package com.bookmysport.backend.slot.service;

import com.bookmysport.backend.venue.entity.PricingRuleEntity;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.common.enums.DayType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Service
public class PriceResolverService {

    public PricingRuleEntity resolvePricingRule(
            SportAreaEntity sportArea,
            LocalDate date,
            LocalTime slotStartTime) {

        DayType dayType = resolveDayType(date);

        return sportArea.getPricingRules()
                .stream()
                .filter(rule -> rule.getDayType() == dayType)
                .filter(rule -> isWithinBand(slotStartTime, rule))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No pricing rule found for SportArea : "
                                        + sportArea.getId()
                                        + " Day : "
                                        + dayType
                                        + " Time : "
                                        + slotStartTime));
    }

    public BigDecimal resolvePrice(
            SportAreaEntity sportArea,
            LocalDate date,
            LocalTime slotStartTime) {

        return resolvePricingRule(sportArea, date, slotStartTime)
                .getPrice();
    }

    private DayType resolveDayType(LocalDate date) {

        DayOfWeek day = date.getDayOfWeek();

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return DayType.WEEKEND;
        }

        return DayType.WEEKDAY;
    }

    private boolean isWithinBand(
            LocalTime slotStartTime,
            PricingRuleEntity rule) {

        return !slotStartTime.isBefore(rule.getBandStart())
                && slotStartTime.isBefore(rule.getBandEnd());
    }

}