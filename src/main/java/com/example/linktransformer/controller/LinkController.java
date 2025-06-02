package com.example.linktransformer.controller;

import com.example.linktransformer.dto.CreateLinkRequest;
import java.util.Optional;
import com.example.linktransformer.dto.LinkResponse;
import com.example.linktransformer.dto.UpdateLinkRequest;
import com.example.linktransformer.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class LinkController {

    private final LinkService linkService;



    public LinkController(LinkService linkService) {
        this.linkService = linkService;

    }


    @PostMapping("/api/links")
    public ResponseEntity<LinkResponse> createLink(@Valid @RequestBody CreateLinkRequest request) {
        LinkResponse response = linkService.createLink(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/api/links/{id}")
    public ResponseEntity<LinkResponse> getLinkInfo(@PathVariable String id) {
        return linkService.getLinkInfo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/red/{id}")
    public ResponseEntity<Void> redirectToTarget(@PathVariable String id) {
        Optional<String> targetUrlOptional = linkService.processRedirectAndCountVisit(id);

        if (targetUrlOptional.isPresent()) {

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, targetUrlOptional.get())
                    .build();
        } else {

            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/api/links/{id}")
    public ResponseEntity<Void> updateLink(@PathVariable String id, @Valid @RequestBody UpdateLinkRequest request) {
        LinkService.UpdateResult result = linkService.updateLink(id, request);
        switch (result) {
            case SUCCESS:
                return ResponseEntity.noContent().build();
            case NOT_FOUND:
                return ResponseEntity.notFound().build();
            case FORBIDDEN_WRONG_PASSWORD:
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .header("reason", "wrong password")
                        .build();
            case FORBIDDEN_LINK_NOT_PROTECTED:
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .header("reason", "Link not updateable as it is not password-protected") // Custom reason
                        .build();

            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/api/links/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable String id,
                                           @RequestParam(name = "pass", required = false) String passwordFromRequest) {
        // The example "pass: abc123" is unusual. Assuming it's a query param for simplicity.
        LinkService.DeleteResult result = linkService.deleteLink(id, passwordFromRequest);
        switch (result) {
            case SUCCESS_OR_NOT_FOUND: // Handles both successful delete and "delete non-existent"
                return ResponseEntity.noContent().build();
            case FORBIDDEN_WRONG_PASSWORD:
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .header("reason", "wrong password")
                        .build();
            case FORBIDDEN_LINK_NOT_PROTECTED:

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .header("reason", "wrong password")
                        .build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
