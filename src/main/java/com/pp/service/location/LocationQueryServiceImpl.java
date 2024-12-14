package com.pp.service.location;

import com.pp.domain.Location;
import com.pp.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationQueryServiceImpl implements LocationQueryService {

    private final LocationRepository locationRepository;

    @Override
    public Location getLocation(Long locationId) {
        return locationRepository.findById(locationId).get();
    }
}
