package com.dev.deep.caching.controller;

import com.dev.deep.caching.entity.Programmer;
import com.dev.deep.caching.service.ProgrammerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/programmers")
@RequiredArgsConstructor
public class ProgrammerController {

    private final ProgrammerService service;

    @PostMapping
    public ResponseEntity<Programmer> create(@RequestBody Programmer programmer) {
        programmer = service.createProgrammer(programmer);
        return ResponseEntity.status(HttpStatus.CREATED).body(programmer);
    }

    @GetMapping
    public ResponseEntity<List<Programmer>> getAll() {
        List<Programmer> programmers = service.getAllProgrammers();
        return ResponseEntity.status(HttpStatus.OK).body(programmers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Programmer> read(@PathVariable Long id) {
        Programmer programmer = service.getProgrammerById(id);
        return ResponseEntity.status(HttpStatus.OK).body(programmer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Programmer> update(@PathVariable Long id, @RequestBody Programmer programmer) {
        programmer = service.updateProgrammer(id, programmer);
        return ResponseEntity.status(HttpStatus.OK).body(programmer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProgrammer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

