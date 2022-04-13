package io.wisoft.webapplication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensingRepository extends JpaRepository<Sensing, Integer> {
}
