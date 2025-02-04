package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {

    @InjectMocks
    private MovieService service;

    @Mock
    private MovieRepository repository;

    private Long existingId, nonExistingId, dependentId;
    private MovieDTO movieDTO;
    private MovieEntity movie;
    private PageImpl<MovieEntity> page;
    private String title;


    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        movie = MovieFactory.createMovieEntity();
        movieDTO = MovieFactory.createMovieDTO();
        page = new PageImpl<>(List.of(movie));
        title = "Test movie";


        when(repository.searchByTitle(anyString(), any(Pageable.class))).thenReturn(page);

        when(repository.findById(existingId)).thenReturn(Optional.of(movie));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(repository.save(any(MovieEntity.class))).thenReturn(movie);

        when(repository.getReferenceById(existingId)).thenReturn(movie);
        when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        when(repository.existsById(existingId)).thenReturn(true);
        when(repository.existsById(dependentId)).thenReturn(true);
        when(repository.existsById(nonExistingId)).thenReturn(false);

        doNothing().when(repository).deleteById(existingId);

        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
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

        MovieDTO result = service.findById(existingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void insertShouldReturnMovieDTO() {

        MovieDTO result = service.insert(movieDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieDTO.getId());
    }

    @Test
    public void updateShouldReturnMovieDTOWhenIdExists() {

        MovieDTO result = service.update(existingId, movieDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingId);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, movieDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

        assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }
}
