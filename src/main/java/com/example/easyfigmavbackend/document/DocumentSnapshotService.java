package com.example.easyfigmavbackend.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentSnapshotService {

    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    public DocumentSnapshotService(DocumentRepository documentRepository, ObjectMapper objectMapper) {
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public JsonNode getSnapshot(UUID documentId) {
        DocumentEntity document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        try {
            return objectMapper.readTree(document.getSnapshotJson());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored document snapshot is not valid JSON", exception);
        }
    }
}
