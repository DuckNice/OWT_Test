package com.example.ContactsAPI.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ContactsAPI.services.CrudService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController()
public abstract class BaseRestController<DB, DT, R extends CrudRepository<DB, Long>, S extends CrudService<DB, DT, R>> {
    @Autowired
    R repo;
    @Autowired
    S service;

    protected abstract String generateObjectUrl(DB entry, HttpServletRequest req);

    // E.g. Break any circular links
    protected abstract DB prepareObjectForReturn(DB entry);

    @GetMapping
    public ResponseEntity<List<DB>> findAll() {
        List<DB> entries = IterableUtils.toList(repo.findAll());
        if (entries.size() == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(entries.stream().map((DB entry) -> prepareObjectForReturn(entry)).toList());
    }

    @PostMapping
    public ResponseEntity<DB> createOne(@RequestBody @Valid DT creationEntry,
            HttpServletRequest req) {
        Optional<DB> newEntry = service.createWUnknownId(creationEntry);

        // Created or conflict
        if (newEntry.isPresent()) {
            return ResponseEntity.created(URI.create(generateObjectUrl(newEntry.get(), req))).body(
                    prepareObjectForReturn(newEntry.get()));
        } else {
            return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DB> findOne(@PathVariable Long id) {
        Optional<DB> entry = repo.findById(id);

        if (entry.isPresent()) {
            return ResponseEntity.ok(prepareObjectForReturn(entry.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DB> updateOne(@PathVariable Long id,
            @RequestBody @Valid DT creationEntry,
            HttpServletRequest req) {
        // Perform save
        Pair<Boolean, Optional<DB>> operationResponse = service.createOrUpdateWKnownId(id,
                creationEntry);
        Optional<DB> newEntry = operationResponse.getSecond();

        // Conflict
        if (newEntry.isEmpty()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(409));
        }

        // Updated or created
        if (operationResponse.getFirst()) {
            return ResponseEntity.ok(newEntry.get());
        } else {
            return ResponseEntity.created(URI.create(generateObjectUrl(newEntry.get(), req))).body(
                    prepareObjectForReturn(newEntry.get()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOne(@PathVariable Long id) {
        Boolean entryExists = repo.existsById(id);
        repo.deleteById(id);

        // Was deleted
        return entryExists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
