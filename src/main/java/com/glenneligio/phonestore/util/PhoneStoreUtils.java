package com.glenneligio.phonestore.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PhoneStoreUtils {

    public void update(Object sourceObj, Object destObj) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(sourceObj, destObj);
    }
}
