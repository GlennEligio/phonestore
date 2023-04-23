package com.glenneligio.phonestore.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.glenneligio.phonestore.controllers.BrandController;
import com.glenneligio.phonestore.service.BrandService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BrandController.class)
public class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    @Test
    void mockMvc() {
        assertNotNull(mockMvc);
    }
}
