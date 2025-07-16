package com.bezkoder.spring.restapi.integration;

import com.bezkoder.spring.restapi.model.Tutorial;
import com.bezkoder.spring.restapi.service.TutorialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class TutorialIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TutorialService tutorialService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        tutorialService.deleteAll();
    }

    @Test
    void testFullCRUDWorkflow() throws Exception {
        // Create a tutorial
        Tutorial newTutorial = new Tutorial("Integration Test Tutorial", "Testing full CRUD workflow", false);
        
        String tutorialJson = mockMvc.perform(post("/api/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTutorial)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Tutorial"))
                .andExpect(jsonPath("$.description").value("Testing full CRUD workflow"))
                .andExpect(jsonPath("$.published").value(false))
                .andReturn().getResponse().getContentAsString();

        Tutorial createdTutorial = objectMapper.readValue(tutorialJson, Tutorial.class);
        Long tutorialId = createdTutorial.getId();

        // Read the created tutorial
        mockMvc.perform(get("/api/tutorials/" + tutorialId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tutorialId))
                .andExpect(jsonPath("$.title").value("Integration Test Tutorial"));

        // Update the tutorial
        Tutorial updatedTutorial = new Tutorial("Updated Integration Test", "Updated description", true);
        mockMvc.perform(put("/api/tutorials/" + tutorialId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTutorial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Integration Test"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.published").value(true));

        // Verify the tutorial appears in published list
        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(tutorialId));

        // Delete the tutorial
        mockMvc.perform(delete("/api/tutorials/" + tutorialId))
                .andExpect(status().isNoContent());

        // Verify the tutorial is deleted
        mockMvc.perform(get("/api/tutorials/" + tutorialId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMultipleTutorialsAndFiltering() throws Exception {
        // Create multiple tutorials
        Tutorial tutorial1 = new Tutorial("Spring Boot Basics", "Learn Spring Boot fundamentals", true);
        Tutorial tutorial2 = new Tutorial("Spring Data JPA", "Database operations with Spring", false);
        Tutorial tutorial3 = new Tutorial("Java Programming", "Core Java concepts", true);

        mockMvc.perform(post("/api/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tutorial1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tutorial2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tutorial3)))
                .andExpect(status().isCreated());

        // Test getting all tutorials
        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        // Test filtering by title
        mockMvc.perform(get("/api/tutorials").param("title", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // Test getting published tutorials
        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // Test delete all
        mockMvc.perform(delete("/api/tutorials"))
                .andExpect(status().isNoContent());

        // Verify all tutorials are deleted
        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testErrorHandling() throws Exception {
        // Test getting non-existent tutorial
        mockMvc.perform(get("/api/tutorials/999"))
                .andExpect(status().isNotFound());

        // Test updating non-existent tutorial
        Tutorial tutorial = new Tutorial("Non-existent", "Description", false);
        mockMvc.perform(put("/api/tutorials/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tutorial)))
                .andExpect(status().isNotFound());

        // Test empty results
        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testTest123Endpoint() throws Exception {
        // Create some test data
        Tutorial tutorial = new Tutorial("Test Tutorial", "Test Description", false);
        mockMvc.perform(post("/api/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tutorial)))
                .andExpect(status().isCreated());

        // Test the test123 endpoint (it has random behavior, so we just check it doesn't crash)
        mockMvc.perform(get("/api/test123"))
                .andExpect(status().isOk());
    }
}