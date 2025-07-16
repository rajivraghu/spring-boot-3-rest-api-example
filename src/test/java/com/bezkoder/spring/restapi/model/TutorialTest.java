package com.bezkoder.spring.restapi.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TutorialTest {

    @Test
    void testDefaultConstructor() {
        Tutorial tutorial = new Tutorial();
        
        assertEquals(0, tutorial.getId());
        assertNull(tutorial.getTitle());
        assertNull(tutorial.getDescription());
        assertFalse(tutorial.isPublished());
    }

    @Test
    void testParameterizedConstructor() {
        String title = "Spring Boot Tutorial";
        String description = "Learn Spring Boot 3";
        boolean published = true;
        
        Tutorial tutorial = new Tutorial(title, description, published);
        
        assertEquals(0, tutorial.getId());
        assertEquals(title, tutorial.getTitle());
        assertEquals(description, tutorial.getDescription());
        assertEquals(published, tutorial.isPublished());
    }

    @Test
    void testSetAndGetId() {
        Tutorial tutorial = new Tutorial();
        long expectedId = 123L;
        
        tutorial.setId(expectedId);
        
        assertEquals(expectedId, tutorial.getId());
    }

    @Test
    void testSetAndGetTitle() {
        Tutorial tutorial = new Tutorial();
        String expectedTitle = "Java Programming";
        
        tutorial.setTitle(expectedTitle);
        
        assertEquals(expectedTitle, tutorial.getTitle());
    }

    @Test
    void testSetAndGetDescription() {
        Tutorial tutorial = new Tutorial();
        String expectedDescription = "Complete Java programming guide";
        
        tutorial.setDescription(expectedDescription);
        
        assertEquals(expectedDescription, tutorial.getDescription());
    }

    @Test
    void testSetAndGetPublished() {
        Tutorial tutorial = new Tutorial();
        
        tutorial.setPublished(true);
        assertTrue(tutorial.isPublished());
        
        tutorial.setPublished(false);
        assertFalse(tutorial.isPublished());
    }

    @Test
    void testToString() {
        Tutorial tutorial = new Tutorial("Test Title", "Test Description", true);
        tutorial.setId(1L);
        
        String expected = "Tutorial [id=1, title=Test Title, desc=Test Description, published=true]";
        String actual = tutorial.toString();
        
        assertEquals(expected, actual);
    }

    @Test
    void testToStringWithNullValues() {
        Tutorial tutorial = new Tutorial();
        
        String expected = "Tutorial [id=0, title=null, desc=null, published=false]";
        String actual = tutorial.toString();
        
        assertEquals(expected, actual);
    }
}