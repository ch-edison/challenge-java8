package com.edisonchavez.challenge.films;

import com.edisonchavez.challenge.films.dto.FilmProps;
import com.edisonchavez.challenge.films.service.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
public class FilmsController {

    private final FilmService service;

    @GetMapping
    public ResponseEntity<Page<FilmProps>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String id) {

        Page<FilmProps> body = service.list(page, size, search, id);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().cachePrivate().mustRevalidate())
                .header(HttpHeaders.VARY, HttpHeaders.AUTHORIZATION)
                .body(body);
    }

    @GetMapping("/{id}")
    public FilmProps film(@PathVariable String id) {
        return service.get(id);
    }
}
