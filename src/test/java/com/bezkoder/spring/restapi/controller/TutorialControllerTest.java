package com.bezkoder.spring.restapi.controller;

import com.bezkoder.spring.restapi.model.Tutorial;
import com.bezkoder.spring.restapi.service.TutorialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TutorialController.class)
class TutorialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TutorialService tutorialService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTutorials_Success() throws Exception {
        List<Tutorial> tutorials = Arrays.asList(
            new Tutorial("Tutorial 1", "Description 1", false),
            new Tutorial("Tutorial 2", "Description 2", true)
        );
        tutorials.get(0).setId(1L);
        tutorials.get(1).setId(2L);

        when(tutorialService.findAll()).thenReturn(tutorials);

        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Tutorial 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Tutorial 2"));
    }

    @Test
    void testGetAllTutorials_WithTitleFilter() throws Exception {
        List<Tutorial> tutorials = Arrays.asList(
            new Tutorial("Spring Boot Tutorial", "Learn Spring Boot", false)
        );
        tutorials.get(0).setId(1L);

        when(tutorialService.findByTitleContaining("Spring")).thenReturn(tutorials);

        mockMvc.perform(get("/api/tutorials").param("title", "Spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring Boot Tutorial"));
    }

    @Test
    void testGetAllTutorials_EmptyResult() throws Exception {
        when(tutorialService.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllTutorials_InternalServerError() throws Exception {
        when(tutorialService.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/tutorials"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetTutorialById_Success() throws Exception {
        Tutorial tutorial = new Tutorial("Test Tutorial", "Test Description", false);
        tutorial.setId(1L);

        when(tutorialService.findById(1L)).thenReturn(tutorial);

        mockMvc.perform(get("/api/tutorials/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Tutorial"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void testGetTutorialById_NotFound() throws Exception {
        when(tutorialService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/tutorials/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTutorial_Success() throws Exception {
        Tutorial inputTutorial = new Tutorial("New Tutorial", "New Description", false);
        Tutorial savedTutorial = new Tutorial("New Tutorial", "New Description", false);
        savedTutorial.setId(1L);

        when(tutorialService.save(any(Tutorial.class))).thenReturn(savedTutorial);

        mockMvc.perform(post("/api/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputTutorial)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Tutorial"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void testCreateTutorial_InternalServerError() throws Exception {
        Tutorial inputTutorial = new Tutorial("New Tutorial", "New Description", false);

        when(tutorialService.save(any(Tutorial.class))).thenThrow(new RuntimeException("Save error"));

        mockMvc.perform(post("/api/tutorials")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputTutorial)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUpdateTutorial_Success() throws Exception {
        Tutorial existingTutorial = new Tutorial("Original Title", "Original Description", false);
        existingTutorial.setId(1L);

        Tutorial updatedTutorial = new Tutorial("Updated Title", "Updated Description", true);
        updatedTutorial.setId(1L);

        when(tutorialService.findById(1L)).thenReturn(existingTutorial);
        when(tutorialService.save(any(Tutorial.class))).thenReturn(updatedTutorial);

        mockMvc.perform(put("/api/tutorials/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTutorial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void testUpdateTutorial_NotFound() throws Exception {
        Tutorial updateTutorial = new Tutorial("Updated Title", "Updated Description", true);

        when(tutorialService.findById(999L)).thenReturn(null);

        mockMvc.perform(put("/api/tutorials/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTutorial)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTutorial_Success() throws Exception {
        doNothing().when(tutorialService).deleteById(1L);

        mockMvc.perform(delete("/api/tutorials/1"))
                .andExpect(status().isNoContent());

        verify(tutorialService, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTutorial_InternalServerError() throws Exception {
        doThrow(new RuntimeException("Delete error")).when(tutorialService).deleteById(1L);

        mockMvc.perform(delete("/api/tutorials/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteAllTutorials_Success() throws Exception {
        doNothing().when(tutorialService).deleteAll();

        mockMvc.perform(delete("/api/tutorials"))
                .andExpect(status().isNoContent());

        verify(tutorialService, times(1)).deleteAll();
    }

    @Test
    void testDeleteAllTutorials_InternalServerError() throws Exception {
        doThrow(new RuntimeException("Delete all error")).when(tutorialService).deleteAll();

        mockMvc.perform(delete("/api/tutorials"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testFindByPublished_Success() throws Exception {
        List<Tutorial> publishedTutorials = Arrays.asList(
            new Tutorial("Published Tutorial 1", "Description 1", true),
            new Tutorial("Published Tutorial 2", "Description 2", true)
        );
        publishedTutorials.get(0).setId(1L);
        publishedTutorials.get(1).setId(2L);

        when(tutorialService.findByPublished(true)).thenReturn(publishedTutorials);

        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].published").value(true))
                .andExpect(jsonPath("$[1].published").value(true));
    }

    @Test
    void testFindByPublished_EmptyResult() throws Exception {
        when(tutorialService.findByPublished(true)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testFindByPublished_InternalServerError() throws Exception {
        when(tutorialService.findByPublished(true)).thenThrow(new RuntimeException("Find error"));

        mockMvc.perform(get("/api/tutorials/published"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testTest123Endpoint_Success() throws Exception {
        List<Tutorial> tutorials = Arrays.asList(
            new Tutorial("Test Tutorial", "Test Description", false)
        );
        tutorials.get(0).setId(1L);

        when(tutorialService.findAll()).thenReturn(tutorials);

        mockMvc.perform(get("/api/test123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}