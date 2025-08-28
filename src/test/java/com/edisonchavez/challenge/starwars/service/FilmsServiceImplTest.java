package com.edisonchavez.challenge.starwars.service;

import com.edisonchavez.challenge.films.dto.FilmProps;
import com.edisonchavez.challenge.films.service.FilmServiceImpl;
import com.edisonchavez.challenge.films.service.FilmsCacheRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FilmsServiceImplTest {

    @Mock
    FilmsCacheRepo repo;
    @InjectMocks
    FilmServiceImpl service;

    @Test
    void listFiltersOrdersAndPaginates() {
        List<FilmProps> data = Arrays.asList(
                film("A New Hope", 4, "https://www.swapi.tech/api/films/1"),
                film("The Empire Strikes Back", 5, "https://www.swapi.tech/api/films/2")
        );
        Mockito.when(repo.fetchAll()).thenReturn(data);

        Page<FilmProps> page = service.list(0, 1, "hope", null);
        assertEquals(1, page.getTotalElements());
        assertEquals("A New Hope", page.getContent().get(0).getTitle());

        Page<FilmProps> byId = service.list(0, 10, null, "2");
        assertEquals(1, byId.getNumberOfElements());
        assertEquals("The Empire Strikes Back", byId.getContent().get(0).getTitle());

        Page<FilmProps> all = service.list(0, 10, null, null);
        assertEquals(
                Arrays.asList(4, 5),
                all.getContent().stream().map(FilmProps::getEpisodeId).collect(Collectors.toList())
        );
    }

    private static FilmProps film(String title, Integer ep, String url){
        FilmProps f = new FilmProps();
        f.setTitle(title); f.setEpisodeId(ep); f.setUrl(url);
        return f;
    }
}
