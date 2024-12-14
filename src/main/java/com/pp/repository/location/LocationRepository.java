package com.pp.repository.location;

import com.pp.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    boolean existsByName(String name);
}
