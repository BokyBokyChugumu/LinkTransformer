package com.example.linktransformer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shortened_links")
@Getter
@Setter
@NoArgsConstructor
public class ShortenedLink {

    @Id
    @Column(length = 10)
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(length = 2048)
    private String targetUrl;

    private String password;

    private long visits = 0;

    public ShortenedLink(String id, String name, String targetUrl, String password) {
        this.id = id;
        this.name = name;
        this.targetUrl = targetUrl;
        this.password = password;
        this.visits = 0;
    }

    public void incrementVisits() {
        this.visits++;
    }
}
