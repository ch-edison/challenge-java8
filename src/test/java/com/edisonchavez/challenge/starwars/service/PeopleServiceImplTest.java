package com.edisonchavez.challenge.starwars.service;

import com.edisonchavez.challenge.people.client.PeopleClient;
import com.edisonchavez.challenge.people.dto.*;
import com.edisonchavez.challenge.people.service.PeopleServiceImpl;
import com.edisonchavez.challenge.shared.FilterResponse;
import com.edisonchavez.challenge.shared.QueryRequest;
import com.edisonchavez.challenge.shared.ResultData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PeopleServiceImplTest {

    @Mock
    private PeopleClient client;

    @InjectMocks
    private PeopleServiceImpl service;

    @Captor
    private ArgumentCaptor<QueryRequest> queryCaptor;

    @BeforeEach
    void setUp() {}

    @Test
    void list_withoutName_usesClientListAndReturnsPage() {
        PageResponse<PersonListProps> ext = new PageResponse<>();
        List<PersonListProps> results = Arrays.asList(
                personList("1", "Luke", "u1"),
                personList("2", "Leia", "u2")
        );
        ext.setResults(results);
        ext.setTotalRecords(82);

        when(client.list()).thenReturn(ext);

        Page<PersonListProps> page = service.list(0, 10, null);

        assertNotNull(page);
        assertEquals(82L, page.getTotalElements(), "Debe respetar el total_records de la API");
        assertEquals(2, page.getContent().size(), "Debe mapear los results tal cual");
        assertEquals("Leia", page.getContent().get(0).getName(), "Debe venir ordenado por nombre (Sort asc)");
        assertEquals("Luke", page.getContent().get(1).getName());
        verify(client, times(1)).list();
        verify(client, never()).listFilter(any());
    }

    @Test
    void list_withName_usesClientListFilter_mapsSortsAndPaginates() {
        List<ResultData<PersonProps>> rows = new ArrayList<>();
        rows.add(result("10", person("Zeb", "uz")));
        rows.add(result("11", person("Anakin", "ua")));
        rows.add(result("12", person("Luke", "ul")));
        rows.add(result("13", person("Leia", "uleia")));
        rows.add(result("14", person("Obi-Wan", "uobi")));

        FilterResponse<PersonProps> ext = new FilterResponse<>();
        ext.setResult(rows);

        when(client.listFilter(any(QueryRequest.class))).thenReturn(ext);

        int pageIndex = 1;
        int size = 2;
        String name = "  luke  ";

        Page<PersonListProps> page = service.list(pageIndex, size, name);

        assertNotNull(page);

        assertEquals(2, page.getContent().size());
        assertEquals("Luke", page.getContent().get(0).getName());
        assertEquals("Obi-Wan", page.getContent().get(1).getName());
        assertEquals(5, page.getTotalElements(), "Total debe ser el tamaño de items filtrados");
        assertEquals(1, page.getNumber());
        assertEquals(2, page.getSize());

        verify(client).listFilter(queryCaptor.capture());
        QueryRequest sent = queryCaptor.getValue();
        assertNotNull(sent);
        assertEquals(pageIndex + 1, sent.getPage(), "apiPage debe ser 1-based");
        assertEquals(size, sent.getLimit(), "apiLimit debe ser el size pedido (clamp interno ya probado en otro test)");
        assertEquals("luke", sent.getName(), "name debe estar trim y sin espacios extremos (tal cual setea el service)");

        verify(client, never()).list();
    }

    @Test
    void getReturnsPropertiesFromClient() {
        String id = "42";
        PersonProps props = person("Mace Windu", "u42");

        ResultData<PersonProps> resultData = new ResultData<>();
        resultData.setUid(id);
        resultData.setProperties(props);

        ApiResponse<ResultData<PersonProps>> api = new ApiResponse<>();
        api.setResult(resultData);

        when(client.get(id)).thenReturn(api);

        PersonProps out = service.get(id);

        assertNotNull(out);
        assertEquals("Mace Windu", out.getName());
        assertEquals("u42", out.getUrl());
        verify(client, times(1)).get(id);
        verifyNoMoreInteractions(client);
    }


    private static PersonListProps personList(String uid, String name, String url) {
        PersonListProps p = new PersonListProps();
        p.setUid(uid);
        p.setName(name);
        p.setUrl(url);
        return p;
    }

    private static PersonProps person(String name, String url) {
        PersonProps p = new PersonProps();
        p.setName(name);
        p.setUrl(url);
        return p;
    }

    private static ResultData<PersonProps> result(String uid, PersonProps props) {
        ResultData<PersonProps> r = new ResultData<>();
        r.setUid(uid);
        r.setProperties(props);
        return r;
    }
}