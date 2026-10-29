package com.example.library.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:librarytest;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.h2.console.enabled=false"
})
@AutoConfigureMockMvc
class BookControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired BookRepository repository;

    @BeforeEach void clean() { repository.deleteAll(); }

    private String payload(String id, String title, String author) throws Exception {
        Book b = new Book();
        b.setBookId(id); b.setTitle(title); b.setAuthor(author); b.setCategory("Fiction");
        return mapper.writeValueAsString(b);
    }

    @Test void createListSearchUpdateStatusAndDelete() throws Exception {
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                .content(payload("BK1", "The Hobbit", "Tolkien")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("AVAILABLE"));

        mvc.perform(get("/api/books")).andExpect(status().isOk()).andExpect(jsonPath("$[0].bookId").value("BK1"));
        mvc.perform(get("/api/books").param("search", "tolkien"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));

        mvc.perform(patch("/api/books/BK1/availability").contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ISSUED\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ISSUED"));

        mvc.perform(put("/api/books/BK1").contentType(MediaType.APPLICATION_JSON)
                .content(payload("BK1", "The Hobbit Updated", "Tolkien")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("The Hobbit Updated"));

        mvc.perform(delete("/api/books/BK1")).andExpect(status().isNoContent());
        mvc.perform(get("/api/books/BK1")).andExpect(status().isNotFound());
    }

    @Test void duplicateBookIdReturnsConflict() throws Exception {
        String body = payload("DUP1", "First", "Author");
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test void missingTitleReturnsBadRequest() throws Exception {
        mvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookId\":\"BK2\",\"title\":\" \",\"author\":\"Writer\",\"category\":\"Drama\"}"))
                .andExpect(status().isBadRequest());
    }
}
