package com.example.linktransformer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLinkRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Target URL cannot be blank")
    @Size(max = 2048, message = "Target URL too long")
    // Basic URL validation can be added with @URL if needed, but not specified
    private String targetUrl;

    private String password; // Optional
}
