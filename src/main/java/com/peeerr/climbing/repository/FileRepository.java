package com.peeerr.climbing.repository;

import com.peeerr.climbing.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
