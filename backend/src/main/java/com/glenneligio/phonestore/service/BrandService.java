package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.entity.BrandEntity;
import com.glenneligio.phonestore.entity.PhoneEntity;
import com.glenneligio.phonestore.repository.BrandRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BrandService {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";

    private BrandRepository brandRepository;
    private ModelMapper mapper;

    @Autowired
    public BrandService(BrandRepository brandRepository, ModelMapper mapper) {
        this.brandRepository = brandRepository;
        this.mapper = mapper;
    }

    public List<BrandEntity> getAllBrands() {
        final String METHOD_NAME = "getAllBrands";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<BrandEntity> brandEntityList = brandRepository.findAll();
        log.info(EXITING_METHOD, METHOD_NAME);
        return brandEntityList;
    }

    public BrandEntity getBrandById(Long brandId) {
        final String METHOD_NAME = "getBrandById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity brandEntity = brandRepository.findById(brandId).orElseThrow(() -> new ApiException("Brand with specified id was not founds", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        return brandEntity;
    }

    public BrandEntity getBrandByName(String name) {
        final String METHOD_NAME = "getBrandByName";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity entity = brandRepository.findByName(name).orElseThrow(() -> new ApiException("Brand with specified name was not found", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        return entity;
    }

    public BrandEntity createBrand(BrandEntity brandEntity) {
        final String METHOD_NAME = "createBrand";
        log.info(ENTERING_METHOD, METHOD_NAME);
        Optional<BrandEntity> brandOptional = brandRepository.findByName(brandEntity.getName());
        if(brandOptional.isPresent()) throw new ApiException("Brand with same name already exist", HttpStatus.BAD_REQUEST);
        brandEntity.setPhoneList(new ArrayList<>());
        BrandEntity brandCreated = brandRepository.save(brandEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        return brandCreated;
    }

    public BrandEntity updateBrandById(Long brandId, BrandEntity brandEntity) {
        final String METHOD_NAME = "updateBrandById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity brandEntity1 = brandRepository.findById(brandId)
                .orElseThrow(() -> new ApiException("Brand with specified id was not found", HttpStatus.NOT_FOUND));
        Optional<BrandEntity> brandOptional = brandRepository.findByName(brandEntity.getName());
        if(brandOptional.isPresent()) throw new ApiException("Brand with same name already exist", HttpStatus.BAD_REQUEST);
        mapper.map(brandEntity, brandEntity1);
        BrandEntity brandUpdated = brandRepository.save(brandEntity1);
        log.info(EXITING_METHOD, METHOD_NAME);
        return brandUpdated;
    }

    public void deleteBrandById(Long brandId) {
        final String METHOD_NAME = "deleteBrandById";
        log.info("Entering methdo {}", METHOD_NAME);
        BrandEntity brandEntity = brandRepository.findById(brandId).orElseThrow(() -> new ApiException("Brand with specified id was not found", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        brandRepository.delete(brandEntity);
    }

    public List<PhoneEntity> getBrandPhones(String brandName) {
        final String METHOD_NAME = "getBrandPhones";
        log.info(ENTERING_METHOD, METHOD_NAME);
        BrandEntity brandEntity = brandRepository.findByName(brandName)
                .orElseThrow(() -> new ApiException("Brand with specified brand name does not exist", HttpStatus.NOT_FOUND));
        List<PhoneEntity> phoneEntityList = brandEntity.getPhoneList();
        log.info(EXITING_METHOD, METHOD_NAME);
        return phoneEntityList;
    }
}
