package com.ordernow.backend.review.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.user.model.entity.Customer;
import com.ordernow.backend.user.model.entity.Merchant;
import com.ordernow.backend.user.model.entity.Role;
import com.ordernow.backend.auth.repository.UserRepository;
import com.ordernow.backend.review.model.dto.ReviewRequest;
import com.ordernow.backend.review.model.entity.Review;
import com.ordernow.backend.review.repository.ReviewRepository;
import com.ordernow.backend.store.model.entity.Store;
import com.ordernow.backend.store.repository.StoreRepository;
import com.ordernow.backend.user.model.entity.User;
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

import java.util.Arrays;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private StoreRepository storeRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        reviewRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        reviewRepository.deleteAll();
        storeRepository.deleteAll();
    }

    private record CustomerSetupResult(String token, Customer customer) {}

    private CustomerSetupResult setupCustomer() throws Exception {
        String testEmail = "customer@test.com";
        String testPassword = "password123";
        String testName = "測試顧客";

        User customerUser = new User(testName, testEmail, testPassword, Role.CUSTOMER);

        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        String responseBody = mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value(testName))
                .andExpect(jsonPath("$.data.email").value(testEmail))
                .andExpect(jsonPath("$.data.avatarUrl").exists())
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = "Bearer " + objectMapper.readTree(responseBody)
                .path("data")
                .path("token")
                .asText();

        Customer customer = objectMapper.readValue(
                objectMapper.readTree(responseBody)
                        .path("data")
                        .toString(),
                Customer.class
        );

        return new CustomerSetupResult(token, customer);
    }

    private Store createTestStore() throws Exception {
        String testEmail = "merchant@example.com";
        String testPassword = "password123";
        String testName = "Test Merchant";
        String testPhone = "0912345678";

        User merchantUser = new User(testName, testEmail, testPassword, Role.MERCHANT);
        merchantUser.setPhoneNumber(testPhone);

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchantUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        User savedUser = userRepository.findByEmail(testEmail);
        assertNotNull(savedUser);
        assertEquals(testName, savedUser.getName());
        assertEquals(testEmail, savedUser.getEmail());
        assertEquals(Role.MERCHANT, savedUser.getRole());
        assertEquals(testPhone, savedUser.getPhoneNumber());
        assertTrue(passwordEncoder.matches(testPassword, savedUser.getPassword()));
        assertInstanceOf(Merchant.class, savedUser);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        String responseBody = mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = "Bearer " + objectMapper.readTree(responseBody)
                .path("data")
                .path("token")
                .asText();

        String storeId = objectMapper.readTree(responseBody)
                .path("data")
                .path("storeId")
                .asText();

        String storeDetailResponse = mockMvc.perform(get("/api/v2/stores/" + storeId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Store store = objectMapper.readValue(
                objectMapper.readTree(storeDetailResponse)
                        .path("data")
                        .toString(),
                Store.class
        );



        return store;
    }

    private void createTestReviews(String userId, String userName) {
        Review review1 = new Review();
        review1.setId("testreview001");
        review1.setUserId(userId);
        review1.setUserName(userName);
        review1.setRating(4.5);
        review1.setComment("很好喝的飲料");
        review1.setAverageSpend(100.0);
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setId("testreview002");
        review2.setUserId(userId);
        review2.setUserName(userName);
        review2.setRating(1.0);
        review2.setComment("不好喝的飲料");
        review2.setAverageSpend(100.0);
        reviewRepository.save(review2);
    }

    @Test
    void testGetReviewsByIds() throws Exception {
        CustomerSetupResult customerResult = setupCustomer();
        Customer customer = customerResult.customer();

        createTestReviews(customer.getId(), customer.getName());

        mockMvc.perform(post("/api/v1/reviews/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList("testreview001", "testreview002"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].id").value("testreview001"))
                .andExpect(jsonPath("$.data[0].rating").value(4.5))
                .andExpect(jsonPath("$.data[0].comment").value("很好喝的飲料"))
                .andExpect(jsonPath("$.data[0].userName").value(customer.getName()))
                .andExpect(jsonPath("$.data[1].id").value("testreview002"))
                .andExpect(jsonPath("$.data[1].rating").value(1.0))
                .andExpect(jsonPath("$.data[1].comment").value("不好喝的飲料"))
                .andExpect(jsonPath("$.data[1].userName").value(customer.getName()));
    }

    @Test
    void testGetReviewsByIdsWithEmptyList() throws Exception {
        mockMvc.perform(post("/api/v1/reviews/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ArrayList<>())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Review id list is empty"));
    }

    @Test
    void testAddReviewToStore() throws Exception {
        CustomerSetupResult customerResult = setupCustomer();
        String customerToken = customerResult.token();
        Store store = createTestStore();

        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(4.5);
        reviewRequest.setComment("服務很好");
        reviewRequest.setAverageSpend(150.0);

        mockMvc.perform(post("/api/v1/reviews/" + store.getId())
                .header("Authorization", customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        Store updatedStore = storeRepository.findById(store.getId()).orElse(null);
        assertNotNull(updatedStore);
        assertEquals(1, updatedStore.getReviewIdList().size());
    }

    @Test
    void testAddReviewToStoreUnauthorized() throws Exception {
        Store store = createTestStore();

        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(4.5);
        reviewRequest.setComment("服務很好");
        reviewRequest.setAverageSpend(150.0);

        mockMvc.perform(post("/api/v1/reviews/" + store.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAddReviewToNonExistentStore() throws Exception {
        CustomerSetupResult customerResult = setupCustomer();
        String customerToken = customerResult.token();

        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(4.5);
        reviewRequest.setComment("服務很好");
        reviewRequest.setAverageSpend(150.0);

        mockMvc.perform(post("/api/v1/reviews/nonexistentstore")
                .header("Authorization", customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddInvalidReview() throws Exception {
        CustomerSetupResult customerResult = setupCustomer();
        String customerToken = customerResult.token();
        Store store = createTestStore();

        ReviewRequest reviewRequest = new ReviewRequest();

        mockMvc.perform(post("/api/v1/reviews/" + store.getId())
                .header("Authorization", customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest());
    }
}