package com.example.linktransformer.repository;

import com.example.linktransformer.model.ShortenedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortenedLinkRepository extends JpaRepository<ShortenedLink, String> {

}
