package com.bezkoder.spring.restapi.service;

import com.bezkoder.spring.restapi.model.Tutorial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TutorialServiceTest {

    private TutorialService tutorialService;

    @BeforeEach
    void setUp() {
        tutorialService = new TutorialService();
        // Clear static data before each test
        List<Tutorial> tutorials = new ArrayList<>();
        ReflectionTestUtils.setField(tutorialService, "tutorials", tutorials);
        ReflectionTestUtils.setField(tutorialService, "id", 0L);
    }

    @Test
    void testFindAll_EmptyList() {
        List<Tutorial> result = tutorialService.findAll();
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSave_NewTutorial() {
        Tutorial tutorial = new Tutorial("Test Title", "Test Description", false);
        
        Tutorial saved = tutorialService.save(tutorial);
        
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        assertEquals("Test Title", saved.getTitle());
        assertEquals("Test Description", saved.getDescription());
        assertFalse(saved.isPublished());
        
        assertEquals(1, tutorialService.findAll().size());
    }

    @Test
    void testSave_UpdateExistingTutorial() {
        Tutorial tutorial = new Tutorial("Original Title", "Original Description", false);
        Tutorial saved = tutorialService.save(tutorial);
        
        saved.setTitle("Updated Title");
        saved.setDescription("Updated Description");
        saved.setPublished(true);
        
        Tutorial updated = tutorialService.save(saved);
        
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription());
        assertTrue(updated.isPublished());
        
        assertEquals(1, tutorialService.findAll().size());
    }

    @Test
    void testFindById_ExistingTutorial() {
        Tutorial tutorial = new Tutorial("Test Title", "Test Description", false);
        Tutorial saved = tutorialService.save(tutorial);
        
        Tutorial found = tutorialService.findById(saved.getId());
        
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Test Title", found.getTitle());
    }

    @Test
    void testFindById_NonExistingTutorial() {
        Tutorial found = tutorialService.findById(999L);
        
        assertNull(found);
    }

    @Test
    void testFindByTitleContaining() {
        tutorialService.save(new Tutorial("Spring Boot Tutorial", "Learn Spring Boot", false));
        tutorialService.save(new Tutorial("Java Programming", "Learn Java", false));
        tutorialService.save(new Tutorial("Spring Data JPA", "Learn Spring Data", false));
        
        List<Tutorial> result = tutorialService.findByTitleContaining("Spring");
        
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTitle().contains("Spring")));
    }

    @Test
    void testFindByTitleContaining_NoMatch() {
        tutorialService.save(new Tutorial("Java Programming", "Learn Java", false));
        
        List<Tutorial> result = tutorialService.findByTitleContaining("Python");
        
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByPublished() {
        tutorialService.save(new Tutorial("Published Tutorial", "Published content", true));
        tutorialService.save(new Tutorial("Draft Tutorial", "Draft content", false));
        tutorialService.save(new Tutorial("Another Published", "More published content", true));
        
        List<Tutorial> publishedTutorials = tutorialService.findByPublished(true);
        List<Tutorial> draftTutorials = tutorialService.findByPublished(false);
        
        assertEquals(2, publishedTutorials.size());
        assertEquals(1, draftTutorials.size());
        assertTrue(publishedTutorials.stream().allMatch(Tutorial::isPublished));
        assertTrue(draftTutorials.stream().noneMatch(Tutorial::isPublished));
    }

    @Test
    void testDeleteById() {
        Tutorial tutorial1 = tutorialService.save(new Tutorial("Tutorial 1", "Description 1", false));
        Tutorial tutorial2 = tutorialService.save(new Tutorial("Tutorial 2", "Description 2", false));
        
        assertEquals(2, tutorialService.findAll().size());
        
        tutorialService.deleteById(tutorial1.getId());
        
        assertEquals(1, tutorialService.findAll().size());
        assertNull(tutorialService.findById(tutorial1.getId()));
        assertNotNull(tutorialService.findById(tutorial2.getId()));
    }

    @Test
    void testDeleteById_NonExistingTutorial() {
        tutorialService.save(new Tutorial("Tutorial 1", "Description 1", false));
        
        assertEquals(1, tutorialService.findAll().size());
        
        tutorialService.deleteById(999L);
        
        assertEquals(1, tutorialService.findAll().size());
    }

    @Test
    void testDeleteAll() {
        tutorialService.save(new Tutorial("Tutorial 1", "Description 1", false));
        tutorialService.save(new Tutorial("Tutorial 2", "Description 2", false));
        
        assertEquals(2, tutorialService.findAll().size());
        
        tutorialService.deleteAll();
        
        assertEquals(0, tutorialService.findAll().size());
    }

    @Test
    void testMultipleTutorials() {
        Tutorial tutorial1 = tutorialService.save(new Tutorial("Tutorial 1", "Description 1", false));
        Tutorial tutorial2 = tutorialService.save(new Tutorial("Tutorial 2", "Description 2", true));
        Tutorial tutorial3 = tutorialService.save(new Tutorial("Tutorial 3", "Description 3", false));
        
        assertEquals(3, tutorialService.findAll().size());
        assertEquals(1L, tutorial1.getId());
        assertEquals(2L, tutorial2.getId());
        assertEquals(3L, tutorial3.getId());
    }
}