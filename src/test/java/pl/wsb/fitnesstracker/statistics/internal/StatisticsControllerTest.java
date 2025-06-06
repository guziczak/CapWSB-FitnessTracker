package pl.wsb.fitnesstracker.statistics.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.wsb.fitnesstracker.exception.api.NotFoundException;
import pl.wsb.fitnesstracker.statistics.api.StatisticsService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for StatisticsController.
 */
@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatisticsService statisticsService;

    private StatisticsDTO testDto;

    @BeforeEach
    void setUp() {
        testDto = new StatisticsDTO();
        testDto.setId(1L);
        testDto.setUserId(1L);
        testDto.setUserEmail("test@example.com");
        testDto.setTotalTrainings(10);
        testDto.setTotalDistance(100.0);
        testDto.setTotalCaloriesBurned(2000);
    }

    @Test
    void createStatistics_shouldReturnCreatedStatistics() throws Exception {
        when(statisticsService.createStatistics(anyLong(), any(StatisticsDTO.class))).thenReturn(testDto);

        mockMvc.perform(post("/v1/statistics/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalTrainings").value(10));

        verify(statisticsService).createStatistics(eq(1L), any(StatisticsDTO.class));
    }

    @Test
    void updateStatistics_shouldReturnUpdatedStatistics() throws Exception {
        when(statisticsService.updateStatistics(anyLong(), any(StatisticsDTO.class))).thenReturn(testDto);

        mockMvc.perform(put("/v1/statistics/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalTrainings").value(10));

        verify(statisticsService).updateStatistics(eq(1L), any(StatisticsDTO.class));
    }

    @Test
    void getStatisticsByUserId_shouldReturnStatistics() throws Exception {
        when(statisticsService.getStatisticsByUserId(1L)).thenReturn(testDto);

        mockMvc.perform(get("/v1/statistics/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));

        verify(statisticsService).getStatisticsByUserId(1L);
    }

    @Test
    void getStatisticsById_shouldReturnStatistics() throws Exception {
        when(statisticsService.getStatisticsById(1L)).thenReturn(testDto);

        mockMvc.perform(get("/v1/statistics/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(statisticsService).getStatisticsById(1L);
    }

    @Test
    void deleteStatistics_shouldReturnNoContent() throws Exception {
        doNothing().when(statisticsService).deleteStatistics(1L);

        mockMvc.perform(delete("/v1/statistics/1"))
                .andExpect(status().isNoContent());

        verify(statisticsService).deleteStatistics(1L);
    }

    @Test
    void findByCaloriesGreaterThan_shouldReturnFilteredStatistics() throws Exception {
        List<StatisticsDTO> dtoList = Arrays.asList(testDto);
        when(statisticsService.findStatisticsByCaloriesGreaterThan(1000)).thenReturn(dtoList);

        mockMvc.perform(get("/v1/statistics/calories-greater-than")
                .param("calories", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].totalCaloriesBurned").value(2000));

        verify(statisticsService).findStatisticsByCaloriesGreaterThan(1000);
    }

    @Test
    void recalculateStatistics_shouldReturnRecalculatedStatistics() throws Exception {
        when(statisticsService.recalculateStatistics(1L)).thenReturn(testDto);

        mockMvc.perform(post("/v1/statistics/users/1/recalculate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalTrainings").value(10));

        verify(statisticsService).recalculateStatistics(1L);
    }

    @Test
    void getStatisticsById_shouldReturn404WhenNotFound() throws Exception {
        when(statisticsService.getStatisticsById(999L))
                .thenThrow(new NotFoundException("Statistics not found"));

        mockMvc.perform(get("/v1/statistics/999"))
                .andExpect(status().isNotFound());
    }
}