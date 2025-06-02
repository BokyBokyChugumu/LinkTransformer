package com.example.linktransformer.service;

import com.example.linktransformer.dto.CreateLinkRequest;
import com.example.linktransformer.dto.LinkResponse;
import com.example.linktransformer.dto.UpdateLinkRequest;
import com.example.linktransformer.model.ShortenedLink;
import com.example.linktransformer.repository.ShortenedLinkRepository;
import com.example.linktransformer.util.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LinkService {

    private final ShortenedLinkRepository linkRepository;
    private final String baseRedirectUrl;

    public LinkService(ShortenedLinkRepository linkRepository,
                       @Value("${app.base-redirect-url}") String configuredBaseUrl) {
        this.linkRepository = linkRepository;
        if (configuredBaseUrl.endsWith("/")) {
            this.baseRedirectUrl = configuredBaseUrl + "red/";
        } else {
            this.baseRedirectUrl = configuredBaseUrl + "/red/";
        }
    }

    @Transactional
    public LinkResponse createLink(CreateLinkRequest request) {
        String id;
        do {
            id = IdGenerator.generateRandomId();
        } while (linkRepository.existsById(id));

        ShortenedLink newLink = new ShortenedLink(id, request.getName(), request.getTargetUrl(), request.getPassword());
        ShortenedLink savedLink = linkRepository.save(newLink);
        return mapToLinkResponse(savedLink);
    }

    public Optional<LinkResponse> getLinkInfo(String id) {
        return linkRepository.findById(id).map(this::mapToLinkResponse);
    }

    @Transactional
    public Optional<String> processRedirectAndCountVisit(String id) {
        Optional<ShortenedLink> linkOptional = linkRepository.findById(id);
        if (linkOptional.isPresent()) {
            ShortenedLink link = linkOptional.get();
            link.incrementVisits();
            linkRepository.save(link);
            return Optional.of(link.getTargetUrl());
        }
        return Optional.empty();
    }

    @Transactional
    public UpdateResult updateLink(String id, UpdateLinkRequest request) {
        Optional<ShortenedLink> linkOptional = linkRepository.findById(id);
        if (linkOptional.isEmpty()) {
            return UpdateResult.NOT_FOUND;
        }

        ShortenedLink link = linkOptional.get();

        // Rule: "Updating ... is only possible for password-protected links."
        if (link.getPassword() == null) {
            // Link is not password-protected, so it cannot be updated.
            // The spec only gives "wrong password" as a reason for 403.
            // This case is underspecified for the reason header.
            // Let's return FORBIDDEN_LINK_NOT_PROTECTED, controller can decide exact 403.
            return UpdateResult.FORBIDDEN_LINK_NOT_PROTECTED;
        }

        // Link is password-protected, check provided password.
        if (request.getPassword() == null || !request.getPassword().equals(link.getPassword())) {
            return UpdateResult.FORBIDDEN_WRONG_PASSWORD;
        }

        // Password is correct. Proceed with update.
        boolean updated = false;
        if (request.getName() != null && !request.getName().isBlank()) {
            link.setName(request.getName());
            updated = true;
        }
        if (request.getTargetUrl() != null && !request.getTargetUrl().isBlank()) {
            link.setTargetUrl(request.getTargetUrl());
            updated = true;
        }

        if (updated) {
            linkRepository.save(link);
        }
        return UpdateResult.SUCCESS;
    }

    @Transactional
    public DeleteResult deleteLink(String id, String passwordFromRequest) {
        Optional<ShortenedLink> linkOptional = linkRepository.findById(id);

        if (linkOptional.isEmpty()) {
            // "remove (non-existent link) ... Response 204 No Content"
            return DeleteResult.SUCCESS_OR_NOT_FOUND;
        }

        ShortenedLink link = linkOptional.get();

        // Rule: "Deleting is only possible for password-protected links."
        if (link.getPassword() == null) {
            // Link is not password-protected, so it cannot be deleted.
            // Similar to update, this specific 403 reason isn't detailed.
            return DeleteResult.FORBIDDEN_LINK_NOT_PROTECTED;
        }

        // Link is password-protected, check provided password.
        // The spec for DELETE example `pass: abc123` is ambiguous for missing password.
        // Let's assume missing password is like wrong password.
        if (passwordFromRequest == null || !passwordFromRequest.equals(link.getPassword())) {
            return DeleteResult.FORBIDDEN_WRONG_PASSWORD;
        }

        // Password is correct. Proceed with delete.
        linkRepository.deleteById(id);
        return DeleteResult.SUCCESS_OR_NOT_FOUND; // Corresponds to 204
    }


    private LinkResponse mapToLinkResponse(ShortenedLink link) {
        return new LinkResponse(
                link.getId(),
                link.getName(),
                link.getTargetUrl(),
                baseRedirectUrl + link.getId(),
                link.getVisits()
        );
    }

    // Enums for service method results to guide controller responses
    public enum UpdateResult {
        SUCCESS, NOT_FOUND, FORBIDDEN_WRONG_PASSWORD, FORBIDDEN_LINK_NOT_PROTECTED
    }
    public enum DeleteResult {
        SUCCESS_OR_NOT_FOUND, FORBIDDEN_WRONG_PASSWORD, FORBIDDEN_LINK_NOT_PROTECTED
    }
}