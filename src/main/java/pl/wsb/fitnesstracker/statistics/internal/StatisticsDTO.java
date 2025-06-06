package pl.wsb.fitnesstracker.statistics.internal;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object for Statistics.
 * Used for transferring statistics data via REST API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsDTO {

    private Long id;
    private Long userId;
    private String userEmail;
    private Integer totalTrainings;
    private Double totalDistance;
    private Integer totalCaloriesBurned;

    /**
     * Default constructor.
     */
    public StatisticsDTO() {
    }

    /**
     * Full constructor for creating StatisticsDTO with all fields.
     *
     * @param id the statistics ID
     * @param userId the user ID
     * @param userEmail the user's email
     * @param totalTrainings the total number of trainings
     * @param totalDistance the total distance covered
     * @param totalCaloriesBurned the total calories burned
     */
    public StatisticsDTO(Long id, Long userId, String userEmail, 
                        Integer totalTrainings, Double totalDistance, Integer totalCaloriesBurned) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.totalTrainings = totalTrainings;
        this.totalDistance = totalDistance;
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getTotalTrainings() {
        return totalTrainings;
    }

    public void setTotalTrainings(Integer totalTrainings) {
        this.totalTrainings = totalTrainings;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Integer getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(Integer totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }
}