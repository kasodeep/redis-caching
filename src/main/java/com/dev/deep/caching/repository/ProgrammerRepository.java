package com.dev.deep.caching.repository;

import com.dev.deep.caching.entity.Programmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgrammerRepository extends JpaRepository<Programmer, Long> {
}
