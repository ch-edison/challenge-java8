package com.edisonchavez.challenge.vehicles.service;

import com.edisonchavez.challenge.vehicles.dto.VehiclesListProps;
import com.edisonchavez.challenge.vehicles.dto.VehiclesProps;
import org.springframework.data.domain.Page;

public interface VehiclesService {
    Page<VehiclesListProps> list(int page, int size, String name);
    VehiclesProps get(String id);
}
