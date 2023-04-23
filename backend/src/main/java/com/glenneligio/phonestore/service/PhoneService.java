package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.repository.PhoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PhoneService {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";

    private PhoneRepository phoneRepository;
    private BrandService brandService;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, BrandService brandService) {
        this.phoneRepository = phoneRepository;
        this.brandService = brandService;
    }

    public List<PhoneEntity> getAllPhones() {
        final String METHOD_NAME = "getAllPhones";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<PhoneEntity> phoneEntityList = phoneRepository.findAll();
        log.info(EXITING_METHOD, METHOD_NAME);
        return phoneEntityList;
    }

    public PhoneEntity getPhoneById(Long id) {
        final String METHOD_NAME = "getPhoneById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        PhoneEntity phone = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        return phone;
    }

    public List<PhoneEntity> getPhonesByBrandName(String name) {
        final String METHOD_NAME = "getPhoneByBrandName";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<PhoneEntity> phoneEntityList = phoneRepository.findByBrandName(name);
        log.info(EXITING_METHOD, METHOD_NAME);
        return phoneEntityList;
    }

    public PhoneEntity createPhone(PhoneEntity phoneEntity) {
        final String METHOD_NAME = "createPhone";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity brandEntity = brandService.getBrandByName(phoneEntity.getBrand().getName());
        phoneEntity.setBrand(brandEntity);
        PhoneEntity phoneCreated = phoneRepository.save(phoneEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        return phoneCreated;
    }

    public PhoneEntity updatePhone(Long id, PhoneEntity phoneEntity) {
        final String METHOD_NAME = "updatePhone";
        log.info(ENTERING_METHOD, METHOD_NAME);
        PhoneEntity phoneEntity1 = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exists", HttpStatus.NOT_FOUND));
        BrandEntity brandEntity = brandService.getBrandByName(phoneEntity.getBrand().getName());
        phoneEntity1.setQuantity(phoneEntity.getQuantity());
        phoneEntity1.setBrand(brandEntity);
        phoneEntity1.setDiscount(phoneEntity.getDiscount());
        phoneEntity1.setDescription(phoneEntity.getDescription());
        phoneEntity1.setPrice(phoneEntity.getPrice());
        phoneEntity1.setSpecification(phoneEntity.getSpecification());
        PhoneEntity phoneUpdated = phoneRepository.save(phoneEntity1);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.info("Updated phone {}", phoneUpdated);
        return phoneUpdated;
    }

    public void deletePhone(Long id) {
        final String METHOD_NAME = "deletePhone";
        log.info(ENTERING_METHOD, METHOD_NAME);
        PhoneEntity phoneEntity = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
        phoneRepository.delete(phoneEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
    }

}
