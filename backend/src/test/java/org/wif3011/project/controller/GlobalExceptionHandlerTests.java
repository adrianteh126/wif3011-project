package org.wif3011.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleMaxSizeException() throws Exception {
        MockMultipartFile largeFile = new MockMultipartFile("file", "largeFile.txt", "text/plain", new byte[1024 * 1024 * 201]);

        mockMvc.perform(multipart("/api/sequential")
                        .file(largeFile)
                        .param("numOfWords", "10")
                        .param("sortAscending", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File size exceeds the limit (200mb)"));
    }
}
