package pl.wsb.fitnesstracker.training.api;

/**
 * Enumeration representing different types of physical activities
 * that can be tracked in the fitness tracker application.
 */
public enum ActivityType {

    RUNNING("Running"),
    CYCLING("Cycling"),
    WALKING("Walking"),
    SWIMMING("Swimming"),
    TENNIS("Tenis");

    private final String displayName;

    /**
     * Constructor for ActivityType enum.
     * 
     * @param displayName the user-friendly display name for the activity type
     */
    ActivityType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the activity type.
     * 
     * @return the user-friendly display name
     */
    public String getDisplayName() {
        return displayName;
    }

}