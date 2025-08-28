package com.edisonchavez.challenge.people.service;

import com.edisonchavez.challenge.people.client.PeopleClient;
import com.edisonchavez.challenge.people.dto.*;
import com.edisonchavez.challenge.shared.Constants;
import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = Constants.CACHE_PEOPLE)
public class PeopleServiceImpl implements PeopleService {

    private static final Collator NAME_COLLATOR;
    private static final Comparator<PersonListProps> BY_NAME;

    static {
        NAME_COLLATOR = Collator.getInstance();
        NAME_COLLATOR.setStrength(Collator.PRIMARY);
        BY_NAME = Comparator
                .comparing(PersonListProps::getName,
                        Comparator.nullsLast(NAME_COLLATOR));
    }

    private final PeopleClient client;

    @Override
    @Cacheable(
            cacheNames = Constants.CACHE_PEOPLE_LIST,
            key = "#root.target.cacheKey(#page, #size, #name)",
            unless = "#result == null || !#result.hasContent()"
    )
    public Page<PersonListProps> list(int page, int size, String name) {
        Pageable pageable  = buildPageable(page, size);
        String   safeName  = sanitizeName(name);

        if (safeName == null) {
            PageResponse<PersonListProps> ext = client.list();
            List<PersonListProps> origin = safeList(ext != null ? ext.getResults() : null);
            List<PersonListProps> sorted = sort(origin);
            long total = ext != null && ext.getTotalRecords() != null
                    ? ext.getTotalRecords()
                    : sorted.size();
            return page(sorted, pageable, total);
        }

        QueryRequest query = buildQuery(pageable, safeName);
        FilterResponse<PersonProps> ext = client.listFilter(query);

        List<ResultData<PersonProps>> rows = safeList(ext != null ? ext.getResult() : null);
        List<PersonListProps> mapped = mapRows(rows);
        List<PersonListProps> sorted = sort(mapped);

        return page(sorted, pageable, sorted.size());
    }

    @Override
    @Cacheable(cacheNames = Constants.CACHE_PEOPLE_DETAIL, key = "#id")
    public PersonProps get(String id) {
        ApiResponse<ResultData<PersonProps>> resp = client.get(id);
        ResultData<PersonProps> result = resp != null ? resp.getResult() : null;
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

    private static List<PersonListProps> sort(List<PersonListProps> items) {
        if (items.isEmpty()) return items;
        List<PersonListProps> copy = new ArrayList<>(items);
        copy.sort(BY_NAME);
        return copy;
    }

    private static Page<PersonListProps> page(List<PersonListProps> sortedAll, Pageable pageable, long total) {
        int start = (int) Math.min(pageable.getOffset(), sortedAll.size());
        int end   = Math.min(start + pageable.getPageSize(), sortedAll.size());
        List<PersonListProps> slice = sortedAll.subList(start, end);
        return new PageImpl<>(slice, pageable, total);
    }

    private static List<PersonListProps> mapRows(List<ResultData<PersonProps>> rows) {
        if (rows.isEmpty()) return Collections.emptyList();
        List<PersonListProps> out = new ArrayList<>(rows.size());
        for (ResultData<PersonProps> r : rows) {
            if (r == null) continue;
            PersonProps p = r.getProperties();
            if (p == null) continue;
            PersonListProps pl = new PersonListProps();
            pl.setUid(r.getUid());
            pl.setName(p.getName());
            pl.setUrl(p.getUrl());
            out.add(pl);
        }
        return out;
    }
}