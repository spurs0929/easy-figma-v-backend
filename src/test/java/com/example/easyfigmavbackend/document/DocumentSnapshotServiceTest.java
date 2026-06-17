package com.example.easyfigmavbackend.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentSnapshotServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DocumentRepository documentRepository;

    private DocumentSnapshotService documentSnapshotService;

    @BeforeEach
    void setUp() {
        documentSnapshotService = new DocumentSnapshotService(documentRepository, objectMapper);
    }

    @Test
    void updateSnapshotStoresSnapshotJson() throws Exception {
        UUID documentId = UUID.randomUUID();
        DocumentEntity document = new DocumentEntity(
                documentId,
                "Untitled",
                "{}",
                Instant.parse("2026-06-16T00:00:00Z"),
                Instant.parse("2026-06-16T00:00:00Z")
        );
        JsonNode snapshot = objectMapper.readTree("""
                {
                  "version": 1,
                  "savedAt": 1710000000000,
                  "elements": {
                    "byId": {},
                    "rootIds": []
                  },
                  "comments": []
                }
                """);

        when(documentRepository.findById(documentId)).thenReturn(Optional.of(document));

        JsonNode result = documentSnapshotService.updateSnapshot(documentId, snapshot);

        assertThat(result).isEqualTo(snapshot);
        assertThat(document.getSnapshotJson()).isEqualTo(snapshot.toString());
        verify(documentRepository).save(document);
    }

    @Test
    void updateSnapshotThrowsWhenDocumentDoesNotExist() throws Exception {
        UUID documentId = UUID.randomUUID();
        JsonNode snapshot = objectMapper.readTree("{\"version\":1}");

        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> documentSnapshotService.updateSnapshot(documentId, snapshot))
                .isInstanceOf(DocumentNotFoundException.class);

        verify(documentRepository).findById(documentId);
        verify(documentRepository, org.mockito.Mockito.never()).save(any());
    }
}
