package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.tests.MovieFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository repository;
    private MovieDTO movieDTO;
    private MovieEntity movie;
    private PageImpl<MovieEntity> page;
	private String title;


    @BeforeEach
    void setUp() throws Exception {
        movie = MovieFactory.createMovieEntity();
        movieDTO = MovieFactory.createMovieDTO();
        page = new PageImpl<>(List.of(movie));
		title = "Test movie";


        when(repository.searchByTitle(anyString(), any(Pageable.class))).thenReturn(page);
    }

    @Test
    public void findAllShouldReturnPagedMovieDTO() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<MovieDTO> result = service.findAll(title, pageable);

		assertThat(result).isNotNull();
        assertThat(result.getTotalPages()).isEqualTo(1);
		assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    public void findByIdShouldReturnMovieDTOWhenIdExists() {
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
    }

    @Test
    public void insertShouldReturnMovieDTO() {
    }

    @Test
    public void updateShouldReturnMovieDTOWhenIdExists() {
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
    }
}
