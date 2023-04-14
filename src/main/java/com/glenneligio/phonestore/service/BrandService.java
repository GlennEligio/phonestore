package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.entity.Brand;
import com.glenneligio.phonestore.entity.Phone;
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

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(Long brandId) {
        return brandRepository.findById(brandId).orElseThrow(() -> new ApiException("Brand with specified id was not founds", HttpStatus.NOT_FOUND));
    }

    public Brand getBrandByName(String name) {
        return brandRepository.findByName(name).orElseThrow(() -> new ApiException("Brand with specified name was not found", HttpStatus.NOT_FOUND));
    }

    public Brand createBrand(Brand brand) {
        Optional<Brand> brandOptional = brandRepository.findByName(brand.getName());
        if(brandOptional.isPresent()) throw new ApiException("Brand with same name already exist", HttpStatus.BAD_REQUEST);
        brand.setPhoneList(new ArrayList<>());
        return brandRepository.save(brand);
    }

    public Brand updateBrandById(Long brandId, Brand brand) {
        Brand brand1 = brandRepository.findById(brandId)
                .orElseThrow(() -> new ApiException("Brand with specified id was not found", HttpStatus.NOT_FOUND));
        Optional<Brand> brandOptional = brandRepository.findByName(brand.getName());
        if(brandOptional.isPresent()) throw new ApiException("Brand with same name already exist", HttpStatus.BAD_REQUEST);
        phoneStoreUtils.update(brand, brand1);
        return brandRepository.save(brand1);
    }

    public void deleteBrandById(Long brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new ApiException("Brand with specified id was not found", HttpStatus.NOT_FOUND));
        brandRepository.delete(brand);
    }

    public List<Phone> getBrandPhones(String brandName) {
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> new ApiException("Brand with specified brand name does not exist", HttpStatus.NOT_FOUND));
        return brand.getPhoneList();
    }
}
