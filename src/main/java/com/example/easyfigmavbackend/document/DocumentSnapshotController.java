package com.example.easyfigmavbackend.document;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents/{documentId}/snapshot")
public class DocumentSnapshotController {

    private final DocumentSnapshotService documentSnapshotService;

    public DocumentSnapshotController(DocumentSnapshotService documentSnapshotService) {
        this.documentSnapshotService = documentSnapshotService;
    }

    @GetMapping
    public JsonNode getSnapshot(@PathVariable UUID documentId) {
        return documentSnapshotService.getSnapshot(documentId);
    }
}
