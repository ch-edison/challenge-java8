package com.edisonchavez.challenge.people;

import com.edisonchavez.challenge.people.dto.PersonListProps;
import com.edisonchavez.challenge.people.dto.PersonProps;
import com.edisonchavez.challenge.people.service.PeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PeopleController {

    private final PeopleService service;

    @GetMapping()
    public ResponseEntity<Page<PersonListProps>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name) {

        Page<PersonListProps> body = service.list(page, size, name);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore().cachePrivate().mustRevalidate())
                .header(HttpHeaders.VARY, HttpHeaders.AUTHORIZATION)
                .body(body);
    }

    @GetMapping("/{id}")
    public PersonProps person(@PathVariable String id) {
        return service.get(id);
    }
}
