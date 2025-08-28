package com.edisonchavez.challenge.vehicles.client;

import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import com.edisonchavez.challenge.vehicles.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "starwars-vehicles",
        url = "${starwars.base-url}",
        path = "/api/vehicles"
)
public interface VehiclesClient {
    @GetMapping
    PageResponse<VehiclesListProps> list();

    @GetMapping
    FilterResponse<VehiclesProps> listFilter(@SpringQueryMap QueryRequest query);

    @GetMapping("/{id}")
    ApiResponse<ResultData<VehiclesProps>> get(@PathVariable("id") String id);
}
