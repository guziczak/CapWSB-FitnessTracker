package pl.wsb.fitnesstracker.statistics.api;

import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.statistics.internal.StatisticsDTO;

/**
 * Mapper for converting between Statistics entity and StatisticsDTO.
 * Provides methods for entity-to-DTO and DTO-to-entity conversions.
 */
@Component
public class StatisticsMapper {

    /**
     * Converts a Statistics entity to StatisticsDTO.
     *
     * @param statistics the Statistics entity to convert
     * @return the converted StatisticsDTO, or null if input is null
     */
    public StatisticsDTO toDto(Statistics statistics) {
        if (statistics == null) {
            return null;
        }

        StatisticsDTO dto = new StatisticsDTO();
        dto.setId(statistics.getId());
        dto.setUserId(statistics.getUser() != null ? statistics.getUser().getId() : null);
        dto.setUserEmail(statistics.getUser() != null ? statistics.getUser().getEmail() : null);
        dto.setTotalTrainings(statistics.getTotalTrainings());
        dto.setTotalDistance(statistics.getTotalDistance());
        dto.setTotalCaloriesBurned(statistics.getTotalCaloriesBurned());

        return dto;
    }

    /**
     * Updates an existing Statistics entity with data from StatisticsDTO.
     * Note: This method does not update the user reference.
     *
     * @param dto the StatisticsDTO containing updated data
     * @param statistics the Statistics entity to update
     */
    public void updateEntity(StatisticsDTO dto, Statistics statistics) {
        if (dto == null || statistics == null) {
            return;
        }

        if (dto.getTotalTrainings() != null) {
            statistics.setTotalTrainings(dto.getTotalTrainings());
        }
        if (dto.getTotalDistance() != null) {
            statistics.setTotalDistance(dto.getTotalDistance());
        }
        if (dto.getTotalCaloriesBurned() != null) {
            statistics.setTotalCaloriesBurned(dto.getTotalCaloriesBurned());
        }
    }
}