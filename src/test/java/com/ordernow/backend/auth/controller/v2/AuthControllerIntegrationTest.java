package com.ordernow.backend.auth.controller.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ordernow.backend.auth.model.dto.LoginRequest;
import com.ordernow.backend.store.repository.StoreRepository;
import com.ordernow.backend.user.model.entity.Customer;
import com.ordernow.backend.user.model.entity.Merchant;
import com.ordernow.backend.user.model.entity.Role;
import com.ordernow.backend.user.model.entity.User;
import com.ordernow.backend.user.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);


    @BeforeEach
    void setUpEach() {
        userRepository.deleteAll();
        storeRepository.deleteAll();
    }   

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @Test
    void testRegisterCustomerSuccessfully() throws Exception {
        String testEmail = "customer@example.com";
        String testPassword = "password123";
        String testName = "Test Customer";

        User customerUser = new User(testName, testEmail, testPassword,Role.CUSTOMER);

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"));

        User savedUser = userRepository.findByEmail(testEmail);
        assertNotNull(savedUser);
        assertEquals(testName, savedUser.getName());
        assertEquals(testEmail, savedUser.getEmail());
        assertEquals(Role.CUSTOMER, savedUser.getRole());
        assertTrue(passwordEncoder.matches(testPassword, savedUser.getPassword()));
        assertInstanceOf(Customer.class, savedUser);
    }

    @Test
    void testRegisterAndLoginFlow() throws Exception {
        String testEmail = "test@example.com";
        String testPassword = "password123";
        String testName = "Test User";

        User user = new User(testName, testEmail, testPassword, Role.CUSTOMER);
        
        mockMvc.perform(post("/api/v2/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        mockMvc.perform(post("/api/v2/auth/login")
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
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void testRegisterWithExistingEmail() throws Exception {
        String testExistingName = "Test User";
        String testExistingEmail = "registered@example.com";
        String testExistingPassword = "registeredpassword";
        String testName = "Test User1";
        String testEmail = "registered@example.com";
        String testPassword = "registeredpassword1";


        User user = new User(testExistingName, testExistingEmail, testExistingPassword, Role.CUSTOMER);
        User user2 = new User(testName, testEmail, testPassword, Role.CUSTOMER);
        
        mockMvc.perform(post("/api/v2/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));
        
        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }


    @Test
    void testRegisterMerchantSuccessfully() throws Exception {
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
    }

    @Test
    void testRegisterWithTooLongName() throws Exception {
        String testEmail = "test@example.com";
        String testPassword = "password123";
        String testName = "This name is way too long and should exceed the twenty character limit";

        User user = new User(testName, testEmail, testPassword, Role.CUSTOMER);
    
        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Name is too long"));

        assertNull(userRepository.findByEmail(testEmail));
    }

    @Test
    void testRegisterWithNullRole() throws Exception {
        String testEmail = "test@example.com";
        String testPassword = "password123";
        String testName = "Test User";

        User user = new User(testName, testEmail, testPassword, null);
        
        mockMvc.perform(post("/api/v2/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Field role can not be null"));

        assertNull(userRepository.findByEmail(testEmail));
    }

    @Test
    void testRegisterWithInvalidRole() throws Exception {
        String testEmail = "test@example.com";
        String testPassword = "password123";
        String testName = "Test User";

        User user = new User(testName, testEmail, testPassword,null);
        String userJson = objectMapper.writeValueAsString(user);
        JsonNode jsonNode = objectMapper.readTree(userJson);
        ((ObjectNode) jsonNode).put("role", "INVALIDROLE");
        String modifiedJson = objectMapper.writeValueAsString(jsonNode);


        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modifiedJson))
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.message").value("Invalid role"));

        assertNull(userRepository.findByEmail(testEmail));
    }

    @Test
    void testLoginWithNonExistingEmail() throws Exception {
        String nonExistingEmail = "nonexisting@example.com";
        String testPassword = "password123";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(nonExistingEmail);
        loginRequest.setPassword(testPassword);

        mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithWrongPassword() throws Exception {
        String testEmail = "test@example.com";
        String correctPassword = "correctpassword";
        String wrongPassword = "wrongpassword";
        String testName = "Test User";

        User user = new User(testName, testEmail, correctPassword, Role.CUSTOMER);
        
        mockMvc.perform(post("/api/v2/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(wrongPassword);

        mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithInvalidRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();

        mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterMerchantWithoutPhoneNumber() throws Exception {
        String testEmail = "merchant@example.com";
        String testPassword = "password123";
        String testName = "Test Merchant";

        User user = new User(testName, testEmail, testPassword, Role.MERCHANT);

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Merchant phone number can not be empty"));

        assertNull(userRepository.findByEmail(testEmail));
    }

    @Test
    void testRegisterAndLoginMerchantFlow() throws Exception {
        String testEmail = "merchant@example.com";
        String testPassword = "password123";
        String testName = "測試商家";
        String testPhone = "0912345678";

        User merchantUser = new User(testName, testEmail, testPassword, Role.MERCHANT);
        merchantUser.setPhoneNumber(testPhone);
        
        mockMvc.perform(post("/api/v2/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(merchantUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.message").value("Success"));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        mockMvc.perform(post("/api/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value(testName))
                .andExpect(jsonPath("$.data.email").value(testEmail))
                .andExpect(jsonPath("$.data.avatarUrl").exists())
                .andExpect(jsonPath("$.data.role").value("MERCHANT"))
                .andExpect(jsonPath("$.data.token").exists());

        User savedUser = userRepository.findByEmail(testEmail);
        assertNotNull(savedUser);
        assertEquals(testName, savedUser.getName());
        assertEquals(testEmail, savedUser.getEmail());
        assertEquals(Role.MERCHANT, savedUser.getRole());
        assertEquals(testPhone, savedUser.getPhoneNumber());
        assertTrue(passwordEncoder.matches(testPassword, savedUser.getPassword()));
        assertInstanceOf(Merchant.class, savedUser);
    }
}