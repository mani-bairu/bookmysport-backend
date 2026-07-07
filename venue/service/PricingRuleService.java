package com.bookmysport.backend.venue.service;

import com.bookmysport.backend.exception.ResourseNotFoundException;
import com.bookmysport.backend.venue.dto.requestdto.CreatePricingRuleRequestDto;
import com.bookmysport.backend.venue.dto.responsedto.PricingRuleResponseDto;
import com.bookmysport.backend.venue.entity.PricingRuleEntity;
import com.bookmysport.backend.venue.entity.SportAreaEntity;
import com.bookmysport.backend.venue.mapper.PricingRuleMapper;
import com.bookmysport.backend.venue.repository.PricingRuleRepository;
import com.bookmysport.backend.venue.repository.SportAreaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class PricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;
    private final SportAreaRepository sportAreaRepository;


    public PricingRuleResponseDto addPricingRule(Long ownerId, Long sportAreaId,
                                                 CreatePricingRuleRequestDto req) {
        SportAreaEntity area = sportAreaRepository.findByIdAndVenue_Owner_Id(sportAreaId, ownerId)
                .orElseThrow(() -> new ResourseNotFoundException("Sport area not found for this owner"));

        PricingRuleEntity rule = PricingRuleMapper.toEntity(req, area);
        return PricingRuleMapper.toResponseDto(pricingRuleRepository.save(rule));
    }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<PricingRuleResponseDto> getPricingRules(Long ownerId, Long sportAreaId) {
        sportAreaRepository.findByIdAndVenue_Owner_Id(sportAreaId, ownerId)
                .orElseThrow(() -> new ResourseNotFoundException("Sport area not found for this owner"));

        return pricingRuleRepository.findBySportArea_Id(sportAreaId)
                .stream().map(PricingRuleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

//    @Transactional
//    public void deletePricingRule(Long ownerId, Long sportAreaId, Long ruleId) {
//        sportAreaRepository.findByIdAndVenue_OwnerId(sportAreaId, ownerId)
//                .orElseThrow(() -> BusinessException.notFound("Sport area not found for this owner"));
//
//        PricingRule rule = pricingRuleRepository.findById(ruleId)
//                .orElseThrow(() -> BusinessException.notFound("Pricing rule not found"));
//
//        pricingRuleRepository.delete(rule);
//    }
}
