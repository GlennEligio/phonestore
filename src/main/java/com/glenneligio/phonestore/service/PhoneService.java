package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.model.Phone;
import com.glenneligio.phonestore.repository.PhoneRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {

    private PhoneRepository phoneRepository;

    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    public Phone getPhoneById(Long id) {
        return phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
    }

    public Phone createPhone(Phone phone) {
        return phoneRepository.save(phone);
    }

    public Phone updatePhone(Long id, Phone phone) {
        Phone phone1 = phoneRepository.findById(id).orElseThrow(() -> new ApiException("Phone with specified id does not exist", HttpStatus.NOT_FOUND));
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
