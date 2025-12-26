package com.trans.asgard.service;

import com.trans.asgard.domain.Entrepot.dto.EntrepotRequestDto;
import com.trans.asgard.domain.Entrepot.dto.EntrepotResponseDto;
import com.trans.asgard.domain.Entrepot.mapper.EntrepotMapper;
import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.Entrepot.repository.EntrepotRepository;
import com.trans.asgard.domain.Entrepot.service.impl.EntrepotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepotServiceImplTest {

    @Mock
    private EntrepotRepository entrepotRepository;

    @Mock
    private EntrepotMapper entrepotMapper;

    @InjectMocks
    private EntrepotServiceImpl entrepotService;

    private Entrepot entrepot;
    private EntrepotRequestDto requestDto;
    private EntrepotResponseDto responseDto;

    @BeforeEach
    void setUp() {
        entrepot = Entrepot.builder()
                .id(1L)
                .nom("Entrepôt Principal Paris")
                .ville("Paris")
                .adresse("12 rue des Entrepôts")
                .build();

        requestDto = new EntrepotRequestDto(
                "Entrepôt Principal Paris",
                "Paris",
                "12 rue des Entrepôts"
        );

        responseDto = new EntrepotResponseDto(
                1L,
                "Entrepôt Principal Paris",
                "Paris",
                "12 rue des Entrepôts"
        );
    }

    @Nested
    @DisplayName("createEntrepot")
    class CreateEntrepotTest {

        @Test
        @DisplayName("Devrait créer un entrepôt correctement")
        void shouldCreateEntrepotSuccessfully() {
            // Arrange
            when(entrepotMapper.toEntity(requestDto)).thenReturn(entrepot);
            when(entrepotRepository.save(any(Entrepot.class))).thenReturn(entrepot);
            when(entrepotMapper.toResponseDto(entrepot)).thenReturn(responseDto);

            // Act
            EntrepotResponseDto result = entrepotService.createEntrepot(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.nom()).isEqualTo("Entrepôt Principal Paris");

            verify(entrepotMapper).toEntity(requestDto);
            verify(entrepotRepository).save(entrepot);
            verify(entrepotMapper).toResponseDto(entrepot);
        }
    }

    @Nested
    @DisplayName("updateEntrepot")
    class UpdateEntrepotTest {

        @Test
        @DisplayName("Devrait mettre à jour un entrepôt existant")
        void shouldUpdateExistingEntrepot() {
            // Arrange
            EntrepotRequestDto updateDto = new EntrepotRequestDto(
                    "Entrepôt Paris Nord - UPDATED",
                    "Saint-Denis",
                    "Zone industrielle"
            );

            Entrepot updatedEntrepot = Entrepot.builder()
                    .id(1L)
                    .nom("Entrepôt Paris Nord - UPDATED")
                    .ville("Saint-Denis")
                    .adresse("Zone industrielle")
                    .build();

            EntrepotResponseDto updatedResponse = new EntrepotResponseDto(
                    1L,
                    "Entrepôt Paris Nord - UPDATED",
                    "Saint-Denis",
                    "Zone industrielle"
            );

            when(entrepotRepository.findById(1L)).thenReturn(Optional.of(entrepot));
            doNothing().when(entrepotMapper).updateFromDto(updateDto, entrepot);
            when(entrepotRepository.save(entrepot)).thenReturn(updatedEntrepot);
            when(entrepotMapper.toResponseDto(updatedEntrepot)).thenReturn(updatedResponse);

            // Act
            EntrepotResponseDto result = entrepotService.updateEntrepot(1L, updateDto);

            // Assert
            assertThat(result.nom()).isEqualTo("Entrepôt Paris Nord - UPDATED");
            assertThat(result.ville()).isEqualTo("Saint-Denis");

            verify(entrepotRepository).findById(1L);
            verify(entrepotMapper).updateFromDto(updateDto, entrepot);
            verify(entrepotRepository).save(entrepot);
        }

        @Test
        @DisplayName("Devrait lever une exception quand l'entrepôt n'existe pas")
        void shouldThrowExceptionWhenUpdatingNonExistingEntrepot() {
            // Arrange
            when(entrepotRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> entrepotService.updateEntrepot(999L, requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Entrepôt non trouvé avec l'ID : 999");

            verify(entrepotRepository).findById(999L);
            verifyNoMoreInteractions(entrepotRepository, entrepotMapper);
        }
    }

    @Nested
    @DisplayName("deleteEntrepot")
    class DeleteEntrepotTest {

        @Test
        @DisplayName("Devrait supprimer un entrepôt existant")
        void shouldDeleteExistingEntrepot() {
            // Arrange
            when(entrepotRepository.existsById(1L)).thenReturn(true);

            // Act
            entrepotService.deleteEntrepot(1L);

            // Assert
            verify(entrepotRepository).existsById(1L);
            verify(entrepotRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Devrait lever une exception si l'entrepôt n'existe pas")
        void shouldThrowExceptionWhenDeletingNonExistingEntrepot() {
            // Arrange
            when(entrepotRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> entrepotService.deleteEntrepot(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Entrepôt non trouvé avec l'ID : 999");

            verify(entrepotRepository).existsById(999L);
            verifyNoMoreInteractions(entrepotRepository);
        }
    }

    @Nested
    @DisplayName("getEntrepotById")
    class GetEntrepotByIdTest {

        @Test
        @DisplayName("Devrait retourner un entrepôt existant")
        void shouldReturnExistingEntrepot() {
            // Arrange
            when(entrepotRepository.findById(1L)).thenReturn(Optional.of(entrepot));
            when(entrepotMapper.toResponseDto(entrepot)).thenReturn(responseDto);

            // Act
            EntrepotResponseDto result = entrepotService.getEntrepotById(1L);

            // Assert
            assertThat(result).isEqualTo(responseDto);
        }

        @Test
        @DisplayName("Devrait lever une exception pour un ID inexistant")
        void shouldThrowExceptionForNonExistingId() {
            // Arrange
            when(entrepotRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> entrepotService.getEntrepotById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Entrepôt non trouvé avec l'ID : 999");
        }
    }

    @Nested
    @DisplayName("getAllEntrepots")
    class GetAllEntrepotsTest {

        @Test
        @DisplayName("Devrait retourner la liste de tous les entrepôts")
        void shouldReturnAllEntrepots() {
            // Arrange
            List<Entrepot> entrepots = List.of(entrepot);
            when(entrepotRepository.findAll()).thenReturn(entrepots);
            when(entrepotMapper.toResponseDto(entrepot)).thenReturn(responseDto);

            // Act
            List<EntrepotResponseDto> result = entrepotService.getAllEntrepots();

            // Assert
            assertThat(result)
                    .hasSize(1)
                    .containsExactly(responseDto);

            verify(entrepotRepository).findAll();
            verify(entrepotMapper).toResponseDto(entrepot);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide quand il n'y a aucun entrepôt")
        void shouldReturnEmptyListWhenNoEntrepots() {
            // Arrange
            when(entrepotRepository.findAll()).thenReturn(List.of());

            // Act
            List<EntrepotResponseDto> result = entrepotService.getAllEntrepots();

            // Assert
            assertThat(result).isEmpty();
        }
    }
}