package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.repository.BrandRepository;
import com.glenneligio.phonestore.util.PhoneStoreUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    private BrandRepository brandRepository;
    private PhoneStoreUtils phoneStoreUtils;

    @Autowired
    public BrandService(BrandRepository brandRepository, PhoneStoreUtils phoneStoreUtils) {
        this.brandRepository = brandRepository;
        this.phoneStoreUtils = phoneStoreUtils;
    }

    public List<BrandEntity> getAllBrands() {
        return brandRepository.findAll();
    }

    public BrandEntity getBrandById(Long brandId) {
        return brandRepository.findById(brandId).orElseThrow(() -> new ApiException("Brand with specified id was not founds", HttpStatus.NOT_FOUND));
    }

    public BrandEntity getBrandByName(String name) {
        return brandRepository.findByName(name).orElseThrow(() -> new ApiException("Brand with specified name was not found", HttpStatus.NOT_FOUND));
    }

    public BrandEntity createBrand(BrandEntity brandEntity) {
        Optional<BrandEntity> brandOptional = brandRepository.findByName(brandEntity.getName());
        if(brandOptional.isPresent()) throw new ApiException("Brand with same name already exist", HttpStatus.BAD_REQUEST);
        brandEntity.setPhoneList(new ArrayList<>());
        return brandRepository.save(brandEntity);
    }

    public BrandEntity updateBrandById(Long brandId, BrandEntity brandEntity) {
        BrandEntity brandEntity1 = brandRepository.findById(brandId)
                .orElseThrow(() -> new ApiException("Brand with specified id was not found", HttpStatus.NOT_FOUND));
        Optional<BrandEntity> brandOptional = brandRepository.findByName(brandEntity.getName());
        if(brandOptional.isPresent()) throw new ApiException("Brand with same name already exist", HttpStatus.BAD_REQUEST);
        phoneStoreUtils.update(brandEntity, brandEntity1);
        return brandRepository.save(brandEntity1);
    }

    public void deleteBrandById(Long brandId) {
        BrandEntity brandEntity = brandRepository.findById(brandId).orElseThrow(() -> new ApiException("Brand with specified id was not found", HttpStatus.NOT_FOUND));
        brandRepository.delete(brandEntity);
    }

    public List<PhoneEntity> getBrandPhones(String brandName) {
        BrandEntity brandEntity = brandRepository.findByName(brandName)
                .orElseThrow(() -> new ApiException("Brand with specified brand name does not exist", HttpStatus.NOT_FOUND));
        return brandEntity.getPhoneList();
    }
}
