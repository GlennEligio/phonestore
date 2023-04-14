package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.entity.Brand;
import com.glenneligio.phonestore.entity.Phone;
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

    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    public Phone getPhoneById(Long id) {
        return phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
    }

    public List<Phone> getPhoneByBrandName(String name) {
        return phoneRepository.findByBrandName(name);
    }

    public Phone createPhone(Phone phone) {
        Brand brand = brandService.getBrandByName(phone.getBrand().getName());
        phone.setBrand(brand);
        return phoneRepository.save(phone);
    }

    public Phone updatePhone(Long id, Phone phone) {
        Phone phone1 = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exists", HttpStatus.NOT_FOUND));
        Brand brand = brandService.getBrandByName(phone.getBrand().getName());
        phone.setBrand(brand);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(phone, phone1);
        return phoneRepository.save(phone1);
    }

    public void deletePhone(Long id) {
        Phone phone = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
        phoneRepository.delete(phone);
    }

}
