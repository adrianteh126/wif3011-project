package org.wif3011.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.wif3011.project.service.BagOfWordService;
import org.wif3011.project.service.SequentialService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class BOWControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SequentialService sequentialService;

    @MockBean
    private BagOfWordService bagOfWordService;

    private MockMultipartFile validFile;

    @BeforeEach
    void setUp() {
        validFile = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test data".getBytes());
    }

    @Test
    void testGetSequentialWordMap() throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, Integer> data = new HashMap<>();
        data.put("word", 1);
        response.put("data", data);
        response.put("elapsed_time", 3); // Example time

        Mockito.when(bagOfWordService.sequentialWordCount(Mockito.any())).thenReturn(response);

        mockMvc.perform(multipart("/api/sequential")
                        .file(validFile)
                        .param("numOfWords", "10")
                        .param("sortAscending", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\":{\"word\":1}, \"elapsed_time\":3}"));
    }

    @Test
    void testGetSequentialWordMapInvalidFile() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "invalid data".getBytes());

        mockMvc.perform(multipart("/api/sequential")
                        .file(invalidFile)
                        .param("numOfWords", "10")
                        .param("sortAscending", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Invalid file format.\"}"));
    }

    @Test
    void testConcurrentWordCount1() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("data", new HashMap<String, Integer>());
        Mockito.when(bagOfWordService.concurrentWordCount1(Mockito.any())).thenReturn(response);

        mockMvc.perform(multipart("/api/concurrent/java-stream")
                        .file(validFile)
                        .param("numOfWords", "10")
                        .param("sortAscending", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\":{}}"));
    }

    @Test
    void testConcurrentWordCount1InvalidFile() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "invalid data".getBytes());

        mockMvc.perform(multipart("/api/concurrent/java-stream")
                        .file(invalidFile)
                        .param("numOfWords", "10")
                        .param("sortAscending", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Invalid file format.\"}"));
    }

    @Test
    void testConcurrentWordCount2() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("data", new HashMap<String, Integer>());
        Mockito.when(bagOfWordService.concurrentWordCount2(Mockito.any())).thenReturn(response);

        mockMvc.perform(multipart("/api/concurrent/fork-join")
                        .file(validFile)
                        .param("numOfWords", "10")
                        .param("sortAscending", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\":{}}"));
    }

    @Test
    void testConcurrentWordCount2InvalidFile() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "invalid data".getBytes());

        mockMvc.perform(multipart("/api/concurrent/fork-join")
                        .file(invalidFile)
                        .param("numOfWords", "10")
                        .param("sortAscending", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Invalid file format.\"}"));
    }

    @Test
    void testComparisonBOW() throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, Long> elapsedTime = new HashMap<>();
        elapsedTime.put("elapsed_time_1", 0L);
        elapsedTime.put("elapsed_time_2", 0L);
        elapsedTime.put("elapsed_time_3", 0L);
        response.put("sequential", elapsedTime);
        response.put("javaStream", elapsedTime);
        response.put("forkJoin", elapsedTime);

        Mockito.when(sequentialService.sequentialWordMapCompare(Mockito.any())).thenReturn(new HashMap<>());
        Mockito.when(bagOfWordService.concurrentWordCount1(Mockito.any())).thenReturn(new HashMap<>());
        Mockito.when(bagOfWordService.concurrentWordCount2(Mockito.any())).thenReturn(new HashMap<>());

        mockMvc.perform(multipart("/api/comparison")
                        .file(validFile))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sequential\":{\"elapsed_time_1\":0,\"elapsed_time_2\":0,\"elapsed_time_3\":0},\"javaStream\":{\"elapsed_time_1\":0,\"elapsed_time_2\":0,\"elapsed_time_3\":0},\"forkJoin\":{\"elapsed_time_1\":0,\"elapsed_time_2\":0,\"elapsed_time_3\":0}}"));
    }

    @Test
    void testComparisonBOWInvalidFile() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "invalid data".getBytes());

        mockMvc.perform(multipart("/api/comparison")
                        .file(invalidFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Invalid file format.\"}"));
    }
}
