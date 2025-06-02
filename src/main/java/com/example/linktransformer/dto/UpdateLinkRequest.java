package com.example.linktransformer.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLinkRequest {

    private String name;

    @Size(max = 2048, message = "Target URL too long")
    private String targetUrl;

    private String password;
}
