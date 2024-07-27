package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
