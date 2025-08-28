package com.edisonchavez.challenge.starships.service;

import com.edisonchavez.challenge.shared.Constants;
import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import com.edisonchavez.challenge.starships.client.StarshipsClient;
import com.edisonchavez.challenge.starships.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = Constants.CACHE_STARTSHIPS)
public class StarshipsServiceImpl implements StarshipsService {

    private static final Collator NAME_COLLATOR;
    private static final Comparator<StarshipsListProps> BY_NAME;

    static {
        NAME_COLLATOR = Collator.getInstance();
        NAME_COLLATOR.setStrength(Collator.PRIMARY);
        BY_NAME = Comparator
                .comparing(StarshipsListProps::getName,
                        Comparator.nullsLast(NAME_COLLATOR));
    }

    private final StarshipsClient client;

    @Override
    @Cacheable(
            cacheNames = Constants.CACHE_STARTSHIPS_LIST,
            key = "#root.target.cacheKey(#page, #size, #name)",
            unless = "#result == null || !#result.hasContent()"
    )
    public Page<StarshipsListProps> list(int page, int size, String name) {
        Pageable pageable  = buildPageable(page, size);
        String   safeName  = sanitizeName(name);

        if (safeName == null) {
            PageResponse<StarshipsListProps> ext = client.list();
            List<StarshipsListProps> origin = safeList(ext != null ? ext.getResults() : null);
            List<StarshipsListProps> sorted = sort(origin);
            long total = ext != null && ext.getTotalRecords() != null
                    ? ext.getTotalRecords()
                    : sorted.size();
            return page(sorted, pageable, total);
        }

        QueryRequest query = buildQuery(pageable, safeName);
        FilterResponse<StarshipsProps> ext = client.listFilter(query);

        List<ResultData<StarshipsProps>> rows = safeList(ext != null ? ext.getResult() : null);
        List<StarshipsListProps> mapped = mapRows(rows);
        List<StarshipsListProps> sorted = sort(mapped);

        return page(sorted, pageable, sorted.size());
    }

    @Override
    @Cacheable(cacheNames = Constants.CACHE_STARTSHIPS_DETAIL, key = "#id")
    public StarshipsProps get(String id) {
        ApiResponse<ResultData<StarshipsProps>> resp = client.get(id);
        ResultData<StarshipsProps> result = resp != null ? resp.getResult() : null;
        return result != null ? result.getProperties() : null;
    }

    public String cacheKey(int page, int size, String name) {
        return new StringBuilder()
                .append(Math.max(0, page))
                .append('|')
                .append(Math.min(Math.max(1, size), Constants.MAX_PAGE_SIZE))
                .append('|')
                .append(sanitizeName(name) != null ? sanitizeName(name) : "")
                .toString();
    }

    private static Pageable buildPageable(int page, int size) {
        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), Constants.MAX_PAGE_SIZE);
        return PageRequest.of(p, s);
    }

    private static String sanitizeName(String name) {
        if (name == null) return null;
        String trimmed = name.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static QueryRequest buildQuery(Pageable pageable, String safeName) {
        int apiPage  = pageable.getPageNumber() + 1;
        int apiLimit = pageable.getPageSize();
        return new QueryRequest(apiPage, apiLimit, safeName);
    }

    private static <T> List<T> safeList(List<T> in) {
        return (in == null) ? Collections.emptyList() : in;
    }

    private static List<StarshipsListProps> sort(List<StarshipsListProps> items) {
        if (items.isEmpty()) return items;
        List<StarshipsListProps> copy = new ArrayList<>(items);
        copy.sort(BY_NAME);
        return copy;
    }

    private static Page<StarshipsListProps> page(List<StarshipsListProps> sortedAll, Pageable pageable, long total) {
        int start = (int) Math.min(pageable.getOffset(), sortedAll.size());
        int end   = Math.min(start + pageable.getPageSize(), sortedAll.size());
        List<StarshipsListProps> slice = sortedAll.subList(start, end);
        return new PageImpl<>(slice, pageable, total);
    }

    private static List<StarshipsListProps> mapRows(List<ResultData<StarshipsProps>> rows) {
        if (rows.isEmpty()) return Collections.emptyList();
        List<StarshipsListProps> out = new ArrayList<>(rows.size());
        for (ResultData<StarshipsProps> r : rows) {
            if (r == null) continue;
            StarshipsProps p = r.getProperties();
            if (p == null) continue;
            StarshipsListProps pl = new StarshipsListProps();
            pl.setUid(r.getUid());
            pl.setName(p.getName());
            pl.setUrl(p.getUrl());
            out.add(pl);
        }
        return out;
    }
}