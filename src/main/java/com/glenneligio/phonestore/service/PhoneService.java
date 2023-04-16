package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.repository.PhoneRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {

    private PhoneRepository phoneRepository;
    private BrandService brandService;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, BrandService brandService) {
        this.phoneRepository = phoneRepository;
        this.brandService = brandService;
    }

    public List<PhoneEntity> getAllPhones() {
        return phoneRepository.findAll();
    }

    public PhoneEntity getPhoneById(Long id) {
        return phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
    }

    public List<PhoneEntity> getPhoneByBrandName(String name) {
        return phoneRepository.findByBrandName(name);
    }

    public PhoneEntity createPhone(PhoneEntity phoneEntity) {
        BrandEntity brandEntity = brandService.getBrandByName(phoneEntity.getBrand().getName());
        phoneEntity.setBrand(brandEntity);
        return phoneRepository.save(phoneEntity);
    }

    public PhoneEntity updatePhone(Long id, PhoneEntity phoneEntity) {
        PhoneEntity phoneEntity1 = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exists", HttpStatus.NOT_FOUND));
        BrandEntity brandEntity = brandService.getBrandByName(phoneEntity.getBrand().getName());
        phoneEntity.setBrand(brandEntity);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(phoneEntity, phoneEntity1);
        return phoneRepository.save(phoneEntity1);
    }

    public void deletePhone(Long id) {
        PhoneEntity phoneEntity = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
        phoneRepository.delete(phoneEntity);
    }

}
