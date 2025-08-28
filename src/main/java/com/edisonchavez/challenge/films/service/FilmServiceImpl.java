package com.edisonchavez.challenge.films.service;

import com.edisonchavez.challenge.exceptions.NotFoundException;
import com.edisonchavez.challenge.films.dto.*;
import com.edisonchavez.challenge.shared.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FilmServiceImpl implements FilmService {

    private static final int MAX_PAGE_SIZE = 100;

    private final FilmsCacheRepo repo;

    public Page<FilmProps> list(
            int page,
            int size,
            String search,
            String id) {

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by(Sort.Order.asc("episodeId")));

        List<FilmProps> filtered = applyFilters(repo.fetchAll(), id, search);
        int start = Math.min((int) pageable.getOffset(), filtered.size());
        int end   = Math.min(start + pageable.getPageSize(), filtered.size());
        List<FilmProps> pageContent = filtered.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    public FilmProps get(String id) {
        FilmProps film = repo.fetchById(id);
        if (film == null) throw new NotFoundException("Film not found: " + id);
        return film;
    }

    private List<FilmProps> applyFilters(List<FilmProps> source, String id, String search) {
        String q = (search != null && !search.isEmpty()) ? search.trim().toLowerCase(Locale.ROOT) : null;

        Comparator<FilmProps> byEpisodeId =
                Comparator.comparing(FilmProps::getEpisodeId, Comparator.nullsLast(Integer::compareTo));

        return source.stream()
                .filter(f -> id == null || PageUtils.matchesIdByUrl(f.getUrl(), id))
                .filter(f -> q == null || PageUtils.containsIgnoreCase(f.getTitle(), q))
                .sorted(byEpisodeId)
                .collect(Collectors.toList());
    }
}