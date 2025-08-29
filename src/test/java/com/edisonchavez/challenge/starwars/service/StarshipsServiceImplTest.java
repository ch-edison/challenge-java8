package com.edisonchavez.challenge.starwars.service;

import com.edisonchavez.challenge.shared.Constants;
import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import com.edisonchavez.challenge.starships.client.StarshipsClient;
import com.edisonchavez.challenge.starships.dto.ApiResponse;
import com.edisonchavez.challenge.starships.dto.PageResponse;
import com.edisonchavez.challenge.starships.dto.StarshipsListProps;
import com.edisonchavez.challenge.starships.dto.StarshipsProps;
import com.edisonchavez.challenge.starships.service.StarshipsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StarshipsServiceImplTest {

    @Mock
    private StarshipsClient client;

    @InjectMocks
    private StarshipsServiceImpl service;

    @Captor
    private ArgumentCaptor<QueryRequest> queryCaptor;

    @BeforeEach
    void setUp() {}

    @Test
    void listWithoutNameUsesClientListSortsAndRespectsTotalRecords() {
        StarshipsListProps a = starshipList("2", "tests", "u2");
        StarshipsListProps b = starshipList("3", null,    "u3");
        StarshipsListProps c = starshipList("1", "test2","u1");

        PageResponse<StarshipsListProps> ext = new PageResponse<StarshipsListProps>();
        ext.setResults(Arrays.asList(a, b, c));
        ext.setTotalRecords(123);

        when(client.list()).thenReturn(ext);

        Page<StarshipsListProps> page = service.list(0, 2, null);

        assertNotNull(page);
        assertEquals(123L, page.getTotalElements());
        assertEquals(2, page.getContent().size());

        assertEquals("test2", page.getContent().get(0).getName());
        assertEquals("tests", page.getContent().get(1).getName());

        Page<StarshipsListProps> page1 = service.list(1, 2, null);
        assertEquals(1, page1.getContent().size());
        assertNull(page1.getContent().get(0).getName());

        verify(client, times(2)).list();
        verifyNoMoreInteractions(client);
    }

    @Test
    void list_withName_buildsQueryCorrectly_mapsAndSorts() {
        String rawName = "  Falcon  ";
        int inPage = 0;
        int inSize = 5;

        FilterResponse<StarshipsProps> ext = new FilterResponse<>();
        ext.setResult(Arrays.asList(
                result("9", props("Millennium Falcon", "https://sw/api/starships/9")),
                result("10", props("Aegis",              "https://sw/api/starships/10"))
        ));
        when(client.listFilter(any(QueryRequest.class))).thenReturn(ext);

        Page<StarshipsListProps> page = service.list(inPage, inSize, rawName);

        verify(client).listFilter(queryCaptor.capture());
        QueryRequest qr = queryCaptor.getValue();
        assertEquals(1, qr.getPage(), "apiPage debe ser page+1");
        assertEquals(inSize, qr.getLimit(), "apiLimit debe ser el size de pageable");
        assertEquals("Falcon", qr.getName(), "name debe ser saneado (trim)");

        assertNotNull(page);
        assertEquals(2, page.getTotalElements(), "total = tamaño del resultado filtrado");
        assertEquals(2, page.getContent().size());

        assertEquals("Aegis", page.getContent().get(0).getName());
        assertEquals("Millennium Falcon", page.getContent().get(1).getName());

        assertEquals("10", page.getContent().get(0).getUid());
        assertEquals("https://sw/api/starships/10", page.getContent().get(0).getUrl());
        assertEquals("9", page.getContent().get(1).getUid());
        assertEquals("https://sw/api/starships/9", page.getContent().get(1).getUrl());

        verifyNoMoreInteractions(client);
    }

    @Test
    void listWithoutNameHandlesNullResponseGracefully() {
        when(client.list()).thenReturn(null);

        Page<StarshipsListProps> page = service.list(0, 10, null);

        assertNotNull(page);
        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
        verify(client).list();
        verifyNoMoreInteractions(client);
    }

    @Test
    void get_returnsProperties_whenPresent() {
        StarshipsProps props = props("Interceptor", "http://sw/api/starships/7");
        ApiResponse<ResultData<StarshipsProps>> api = new ApiResponse<>();
        api.setResult(result("7", props));
        when(client.get("7")).thenReturn(api);

        StarshipsProps out = service.get("7");

        assertNotNull(out);
        assertEquals("Interceptor", out.getName());
        assertEquals("http://sw/api/starships/7", out.getUrl());
        verify(client).get("7");
        verifyNoMoreInteractions(client);
    }

    @Test
    void get_returnsNull_whenNoResult() {
        ApiResponse<ResultData<StarshipsProps>> api = new ApiResponse<>();
        api.setResult(null);
        when(client.get("404")).thenReturn(api);

        StarshipsProps out = service.get("404");
        assertNull(out);
        verify(client).get("404");
        verifyNoMoreInteractions(client);
    }

    @Test
    void cacheKey_clampsAndSanitizes() {
        String k1 = service.cacheKey(-1, 0, null);
        assertEquals("0|1|", k1);

        String k2 = service.cacheKey(3, Constants.MAX_PAGE_SIZE + 50, "  Falcon  ");
        assertEquals("3|" + Constants.MAX_PAGE_SIZE + "|Falcon", k2);

        String k3 = service.cacheKey(1, 10, "   ");
        assertEquals("1|10|", k3);
    }

    private static StarshipsListProps starshipList(String uid, String name, String url) {
        StarshipsListProps p = new StarshipsListProps();
        p.setUid(uid);
        p.setName(name);
        p.setUrl(url);
        return p;
    }

    private static StarshipsProps props(String name, String url) {
        StarshipsProps p = new StarshipsProps();
        p.setName(name);
        p.setUrl(url);
        return p;
    }

    private static ResultData<StarshipsProps> result(String uid, StarshipsProps props) {
        ResultData<StarshipsProps> r = new ResultData<>();
        r.setUid(uid);
        r.setProperties(props);
        return r;
    }
}