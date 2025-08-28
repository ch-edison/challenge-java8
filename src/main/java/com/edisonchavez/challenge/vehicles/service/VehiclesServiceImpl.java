package com.edisonchavez.challenge.vehicles.service;

import com.edisonchavez.challenge.shared.Constants;
import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import com.edisonchavez.challenge.vehicles.client.VehiclesClient;
import com.edisonchavez.challenge.vehicles.dto.*;
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
@CacheConfig(cacheNames = Constants.CACHE_VEHICLE)
public class VehiclesServiceImpl implements VehiclesService {

    private static final Collator NAME_COLLATOR;
    private static final Comparator<VehiclesListProps> BY_NAME;

    static {
        NAME_COLLATOR = Collator.getInstance();
        NAME_COLLATOR.setStrength(Collator.PRIMARY);
        BY_NAME = Comparator
                .comparing(VehiclesListProps::getName,
                        Comparator.nullsLast(NAME_COLLATOR));
    }

    private final VehiclesClient client;

    @Override
    @Cacheable(
            cacheNames = Constants.CACHE_VEHICLE_LIST,
            key = "#root.target.cacheKey(#page, #size, #name)",
            unless = "#result == null || !#result.hasContent()"
    )
    public Page<VehiclesListProps> list(int page, int size, String name) {
        Pageable pageable  = buildPageable(page, size);
        String   safeName  = sanitizeName(name);

        if (safeName == null) {
            PageResponse<VehiclesListProps> ext = client.list();
            List<VehiclesListProps> origin = safeList(ext != null ? ext.getResults() : null);
            List<VehiclesListProps> sorted = sort(origin);
            long total = ext != null && ext.getTotalRecords() != null
                    ? ext.getTotalRecords()
                    : sorted.size();
            return page(sorted, pageable, total);
        }

        QueryRequest query = buildQuery(pageable, safeName);
        FilterResponse<VehiclesProps> ext = client.listFilter(query);

        List<ResultData<VehiclesProps>> rows = safeList(ext != null ? ext.getResult() : null);
        List<VehiclesListProps> mapped = mapRows(rows);
        List<VehiclesListProps> sorted = sort(mapped);

        return page(sorted, pageable, sorted.size());
    }

    @Override
    @Cacheable(cacheNames = Constants.CACHE_PEOPLE_DETAIL, key = "#id")
    public VehiclesProps get(String id) {
        ApiResponse<ResultData<VehiclesProps>> resp = client.get(id);
        ResultData<VehiclesProps> result = resp != null ? resp.getResult() : null;
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

    private static List<VehiclesListProps> sort(List<VehiclesListProps> items) {
        if (items.isEmpty()) return items;
        List<VehiclesListProps> copy = new ArrayList<>(items);
        copy.sort(BY_NAME);
        return copy;
    }

    private static Page<VehiclesListProps> page(List<VehiclesListProps> sortedAll, Pageable pageable, long total) {
        int start = (int) Math.min(pageable.getOffset(), sortedAll.size());
        int end   = Math.min(start + pageable.getPageSize(), sortedAll.size());
        List<VehiclesListProps> slice = sortedAll.subList(start, end);
        return new PageImpl<>(slice, pageable, total);
    }

    private static List<VehiclesListProps> mapRows(List<ResultData<VehiclesProps>> rows) {
        if (rows.isEmpty()) return Collections.emptyList();
        List<VehiclesListProps> out = new ArrayList<>(rows.size());
        for (ResultData<VehiclesProps> r : rows) {
            if (r == null) continue;
            VehiclesProps p = r.getProperties();
            if (p == null) continue;
            VehiclesListProps pl = new VehiclesListProps();
            pl.setUid(r.getUid());
            pl.setName(p.getName());
            pl.setUrl(p.getUrl());
            out.add(pl);
        }
        return out;
    }
}