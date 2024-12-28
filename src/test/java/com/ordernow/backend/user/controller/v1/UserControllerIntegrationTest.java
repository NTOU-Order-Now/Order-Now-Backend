package com.ordernow.backend.user.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.user.model.dto.UserProfileRequest;
import com.ordernow.backend.user.model.entity.*;
import com.ordernow.backend.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    String testCustomerEmail = "customer@example.com";
    String testCustomerPassword = "password123";
    String testCustomerName = "測試顧客";
    String testCustomerPhone = "0912745678";

    String testMerchantEmail = "merchant@example.com";
    String testMerchantPassword = "password123";
    String testMerchantName = "測試商家";
    String testMerchantPhone = "0912345678";
    String customerToken;
    String merchantToken;
    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        customerToken = setupCustomer();
        merchantToken = setupMerchant();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }



    private String setupCustomer() throws Exception {

        User customerUser = new User(testCustomerName, testCustomerEmail, testCustomerPassword, Role.CUSTOMER);
        customerUser.setPhoneNumber(testCustomerPhone);

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testCustomerEmail);
        loginRequest.setPassword(testCustomerPassword);

        String responseBody = mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value(testCustomerName))
                .andExpect(jsonPath("$.data.email").value(testCustomerEmail))
                .andExpect(jsonPath("$.data.avatarUrl").exists())
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(responseBody)
                .path("data")
                .path("token")
                .asText();

        return "Bearer " + token;
    }

    private String setupMerchant() throws Exception {

        User merchantUser = new User(testMerchantName, testMerchantEmail, testMerchantPassword, Role.MERCHANT);
        merchantUser.setPhoneNumber(testMerchantPhone);

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchantUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testMerchantEmail);
        loginRequest.setPassword(testMerchantPassword);

        String responseBody = mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value(testMerchantName))
                .andExpect(jsonPath("$.data.email").value(testMerchantEmail))
                .andExpect(jsonPath("$.data.avatarUrl").exists())
                .andExpect(jsonPath("$.data.role").value("MERCHANT"))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(responseBody)
                .path("data")
                .path("token")
                .asText();

        return "Bearer " + token;
    }

    @Test
    void testUpdateCustomerProfile() throws Exception {

        Customer customer = (Customer) userRepository.findByEmail(testCustomerEmail);
        assertNotNull(customer);
        assertEquals(testCustomerName, customer.getName());

        UserProfileRequest profileRequest = new UserProfileRequest();
        profileRequest.setName("更新後的顧客名稱");
        profileRequest.setPhoneNumber("0987654321");
        profileRequest.setAvatarUrl("https://example.com/new-avatar.jpg");
        profileRequest.setGender(Gender.FEMALE);

        mockMvc.perform(put("/api/v1/user")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        User updatedUser = userRepository.findById(customer.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("更新後的顧客名稱", updatedUser.getName());
        assertEquals("0987654321", updatedUser.getPhoneNumber());
        assertEquals("https://example.com/new-avatar.jpg", updatedUser.getAvatarUrl());
        assertEquals(Gender.FEMALE, updatedUser.getGender());
    }

    @Test
    void testUpdateMerchantProfile() throws Exception {

        Merchant merchant = (Merchant) userRepository.findByEmail(testMerchantEmail);
        assertNotNull(merchant);
        assertEquals("測試商家", merchant.getName());
        assertEquals("0912345678", merchant.getPhoneNumber());

        UserProfileRequest profileRequest = new UserProfileRequest();
        profileRequest.setName("更新後的商家名稱");
        profileRequest.setPhoneNumber("0923456789");
        profileRequest.setAvatarUrl("https://example.com/merchant-avatar.jpg");
        profileRequest.setGender(Gender.MALE);

        mockMvc.perform(put("/api/v1/user")
                        .header("Authorization", merchantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        User updatedUser = userRepository.findById(merchant.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertEquals("更新後的商家名稱", updatedUser.getName());
        assertEquals("0923456789", updatedUser.getPhoneNumber());
        assertEquals("https://example.com/merchant-avatar.jpg", updatedUser.getAvatarUrl());
        assertEquals(Gender.MALE, updatedUser.getGender());
    }

    @Test
    void testUpdateProfileUnauthorized() throws Exception {
        UserProfileRequest profileRequest = new UserProfileRequest();
        profileRequest.setName("未授權的更新測試");
        profileRequest.setPhoneNumber("0912345678");

        mockMvc.perform(put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateProfileWithInvalidRequest() throws Exception {

        UserProfileRequest profileRequest = new UserProfileRequest();

        profileRequest.setName("");
        profileRequest.setPhoneNumber("0912345678");

        mockMvc.perform(put("/api/v1/user")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProfileWithNonExistentUser() throws Exception {

        Customer customer = (Customer) userRepository.findByEmail(testCustomerEmail);
        assertNotNull(customer, "Customer should exist before deletion");

        userRepository.deleteById(customer.getId());

        Customer deletedCustomer = (Customer) userRepository.findByEmail(testCustomerEmail);
        assertNull(deletedCustomer, "Customer should be deleted");

        UserProfileRequest profileRequest = new UserProfileRequest();
        profileRequest.setName("測試名稱");
        profileRequest.setPhoneNumber("0912345678");
        profileRequest.setAvatarUrl("https://example.com/avatar.jpg");
        profileRequest.setGender(Gender.MALE);

        mockMvc.perform(put("/api/v1/user")
                        .header("Authorization", customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}