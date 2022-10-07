package io.wisoft.webapplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SensingRepository extends JpaRepository<Sensing, UUID> {
}
