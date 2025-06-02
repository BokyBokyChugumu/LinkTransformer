package com.example.linktransformer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkResponse {
    private String id;
    private String name;
    private String targetUrl;
    private String redirectUrl;
    private long visits;
}