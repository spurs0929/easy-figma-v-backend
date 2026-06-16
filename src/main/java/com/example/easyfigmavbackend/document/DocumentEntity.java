package com.example.easyfigmavbackend.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private String snapshotJson;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected DocumentEntity() {
    }

    public DocumentEntity(UUID id, String name, String snapshotJson, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.snapshotJson = snapshotJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getSnapshotJson() {
        return snapshotJson;
    }
}
