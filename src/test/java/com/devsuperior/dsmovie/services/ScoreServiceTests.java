package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

    @InjectMocks
    private ScoreService service;
    @Mock
    private ScoreRepository scoreRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserService userService;

    private ScoreDTO scoreDTO, nonExistingMovieIdScoreDTO;
    private ScoreEntity score;
    private UserEntity loggedUser;
    private MovieDTO movieDTO;
    private MovieEntity movie;
    private Double scoreValue;

    @BeforeEach
    void setUp() {
        scoreDTO = ScoreFactory.createScoreDTO();
        nonExistingMovieIdScoreDTO = ScoreFactory.createScoreDTO();
        score = ScoreFactory.createScoreEntity();
        loggedUser = UserFactory.createUserEntity();
        movie = MovieFactory.createMovieEntity();
        movieDTO = MovieFactory.createMovieDTO();
        scoreValue = 4.5;

        when(userService.authenticated()).thenReturn(loggedUser);

        when(scoreRepository.saveAndFlush(any(ScoreEntity.class))).thenReturn(score);

        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movie);

    }

    @Test
    public void saveScoreShouldReturnMovieDTO() {
        when(movieRepository.findById(anyLong())).thenReturn(Optional.of(movie));
        movie.getScores().add(score);

        MovieDTO result = service.saveScore(scoreDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(movieDTO.getId());
        assertThat(result.getScore()).isEqualTo(scoreValue);

    }

    @Test
    public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
        when(movieRepository.findById(nonExistingMovieIdScoreDTO.getMovieId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.saveScore(nonExistingMovieIdScoreDTO);
        });
    }
}
