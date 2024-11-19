package com.dev.deep.caching.service;

import com.dev.deep.caching.entity.Programmer;
import com.dev.deep.caching.repository.ProgrammerRepository;
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
    @Cacheable(value = "programmers", key = "#id")
    public Programmer getProgrammerById(Long id) {
        return programmerRepository
                .findById(id)
                .orElse(null);
    }

    /**
     * Method to update the programmer, and change its values.
     */
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
}
