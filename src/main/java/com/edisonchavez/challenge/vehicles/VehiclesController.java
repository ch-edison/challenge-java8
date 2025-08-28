package com.edisonchavez.challenge.vehicles;

import com.edisonchavez.challenge.vehicles.dto.VehiclesListProps;
import com.edisonchavez.challenge.vehicles.dto.VehiclesProps;
import com.edisonchavez.challenge.vehicles.service.VehiclesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehiclesController {

    private final VehiclesService service;

    @GetMapping()
    public ResponseEntity<Page<VehiclesListProps>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name) {

        Page<VehiclesListProps> body = service.list(page, size, name);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().cachePrivate().mustRevalidate())
                .header(HttpHeaders.VARY, HttpHeaders.AUTHORIZATION)
                .body(body);
    }

    @GetMapping("/{id}")
    public VehiclesProps person(@PathVariable String id) {
        return service.get(id);
    }
}
