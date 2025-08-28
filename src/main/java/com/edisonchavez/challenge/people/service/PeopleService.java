package com.edisonchavez.challenge.people.service;

import com.edisonchavez.challenge.people.dto.*;
import org.springframework.data.domain.Page;

public interface PeopleService {
    Page<PersonListProps> list(int page, int size, String name);
    PersonProps get(String id);
}
