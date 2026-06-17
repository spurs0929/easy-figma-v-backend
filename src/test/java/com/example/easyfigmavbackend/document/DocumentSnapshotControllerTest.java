package com.example.easyfigmavbackend.document;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentSnapshotController.class)
class DocumentSnapshotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DocumentSnapshotService documentSnapshotService;

    @Test
    void getSnapshotReturnsStoredDocumentSnapshot() throws Exception {
        UUID documentId = UUID.randomUUID();
        String snapshotJson = """
                {
                  "version": 1,
                  "savedAt": 1710000000000,
                  "elements": {
                    "byId": {},
                    "rootIds": []
                  },
                  "comments": []
                }
                """;

        when(documentSnapshotService.getSnapshot(documentId)).thenReturn(objectMapper.readTree(snapshotJson));

        mockMvc.perform(get("/api/documents/{documentId}/snapshot", documentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.elements.rootIds").isArray())
                .andExpect(jsonPath("$.comments").isArray());
    }

    @Test
    void getSnapshotReturnsNotFoundWhenDocumentDoesNotExist() throws Exception {
        UUID documentId = UUID.randomUUID();
        when(documentSnapshotService.getSnapshot(documentId)).thenThrow(new DocumentNotFoundException(documentId));

        mockMvc.perform(get("/api/documents/{documentId}/snapshot", documentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void updateSnapshotStoresAndReturnsDocumentSnapshot() throws Exception {
        UUID documentId = UUID.randomUUID();
        String snapshotJson = """
                {
                  "version": 1,
                  "savedAt": 1710000000000,
                  "elements": {
                    "byId": {},
                    "rootIds": []
                  },
                  "comments": []
                }
                """;
        JsonNode snapshot = objectMapper.readTree(snapshotJson);

        when(documentSnapshotService.updateSnapshot(documentId, snapshot)).thenReturn(snapshot);

        mockMvc.perform(put("/api/documents/{documentId}/snapshot", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(snapshotJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.savedAt").value(1710000000000L))
                .andExpect(jsonPath("$.elements.byId").isMap());
    }

    @Test
    void updateSnapshotReturnsNotFoundWhenDocumentDoesNotExist() throws Exception {
        UUID documentId = UUID.randomUUID();
        String snapshotJson = """
                {
                  "version": 1,
                  "savedAt": 1710000000000,
                  "elements": {
                    "byId": {},
                    "rootIds": []
                  },
                  "comments": []
                }
                """;
        JsonNode snapshot = objectMapper.readTree(snapshotJson);

        when(documentSnapshotService.updateSnapshot(documentId, snapshot))
                .thenThrow(new DocumentNotFoundException(documentId));

        mockMvc.perform(put("/api/documents/{documentId}/snapshot", documentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(snapshotJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
