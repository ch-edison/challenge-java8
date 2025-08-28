package com.edisonchavez.challenge.starships;

import com.edisonchavez.challenge.people.dto.PersonListProps;
import com.edisonchavez.challenge.shared.PageResult;
import com.edisonchavez.challenge.starships.dto.StarshipsListProps;
import com.edisonchavez.challenge.starships.dto.StarshipsProps;
import com.edisonchavez.challenge.starships.service.StarshipsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/starships")
@RequiredArgsConstructor
public class StarshipsController {

    private final StarshipsService service;

    @GetMapping()
    public ResponseEntity<Page<StarshipsListProps>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name) {

        Page<StarshipsListProps> body = service.list(page, size, name);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().cachePrivate().mustRevalidate())
                .header(HttpHeaders.VARY, HttpHeaders.AUTHORIZATION)
                .body(body);
    }

    @GetMapping("/{id}")
    public StarshipsProps person(@PathVariable String id) {
        return service.get(id);
    }
}
