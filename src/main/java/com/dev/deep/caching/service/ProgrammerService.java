package com.dev.deep.caching.service;

import com.dev.deep.caching.entity.Programmer;
import com.dev.deep.caching.repository.ProgrammerRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgrammerService {

    private final ProgrammerRepository programmerRepository;

    /**
     * Method to save the programmer to database.
     */
    @RateLimiter(name = "programmerService", fallbackMethod = "fallbackRateLimited")
    @CachePut(value = "programmers", key = "#result.id")
    public Programmer createProgrammer(Programmer programmer) {
        return programmerRepository.save(programmer);
    }

    /**
     * Method to get all the programmer of the team.
     */
    @Cacheable(value = "programmers", key = "'all'")
    public List<Programmer> getAllProgrammers() {
        return programmerRepository.findAll();
    }

    /**
     * Method to get a particular programmer by ID.
     */
    @Retry(name = "programmerService", fallbackMethod = "fallbackGetProgrammerById")
    @Cacheable(value = "programmers", key = "#id")
    public Programmer getProgrammerById(Long id) {
        return programmerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Programmer not found"));
    }

    /**
     * Method to update the programmer, and change its values.
     */
    @CircuitBreaker(name = "programmerService", fallbackMethod = "getProgrammerFallback")
    @CacheEvict(value = "programmers", key = "'all'")
    @CachePut(value = "programmers", key = "#id")
    public Programmer updateProgrammer(Long id, Programmer programmer) {
        Programmer existing = programmerRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Programmer not found"));

        existing.setName(programmer.getName());
        existing.setLanguage(programmer.getLanguage());
        return programmerRepository.save(existing);
    }

    /**
     * Method to delete a specific programmer.
     */
    @Bulkhead(name = "programmerService", fallbackMethod = "fallbackBulkhead")
    @CacheEvict(value = "programmers", key = "#id")
    public void deleteProgrammer(Long id) {
        programmerRepository.deleteById(id);
    }

    /**
     * Method to clear all programmers from cache (if needed).
     */
    @CacheEvict(value = "programmers", allEntries = true)
    public void clearAllProgrammersCache() {
        // No operation needed; annotation handles cache clearing
    }

    /**
     * fallBack methods.
     */
    public Programmer fallbackGetProgrammerById(Long id, Throwable t) {
        System.out.println("Retry fallback triggered: " + t.getMessage());
        return null;
    }

    public Programmer fallbackRateLimited(Programmer programmer, Throwable t) {
        System.out.println("Rate limit fallback: " + t.getMessage());
        return null;
    }

    public void fallbackBulkhead(Long id, Throwable t) {
        System.out.println("Bulkhead fallback: " + t.getMessage());
    }
}
