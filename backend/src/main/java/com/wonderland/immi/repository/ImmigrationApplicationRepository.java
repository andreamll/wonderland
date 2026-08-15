package com.wonderland.immi.repository;

import com.wonderland.immi.entity.ImmigrationApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImmigrationApplicationRepository
        extends JpaRepository<ImmigrationApplication, Long> {
}