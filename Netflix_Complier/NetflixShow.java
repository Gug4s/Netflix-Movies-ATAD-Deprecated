import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NetflixShow implements Comparable<NetflixShow> {
    private String showId;
    private String type;
    private String title;
    private LocalDate dateAdded;
    private int releaseYear;
    private String rating;
    private int duration;

    public NetflixShow(String showId, String type, String title, String dateStr, 
                       int releaseYear, String rating, String durationStr) {
        this.showId = showId;
        this.type = type;
        this.title = title;
        this.releaseYear = releaseYear;
        this.rating = rating;
        
        try {
            if (dateStr != null && !dateStr.isBlank()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
                this.dateAdded = LocalDate.parse(dateStr.trim(), formatter);
            }
        } catch (Exception e) {
            this.dateAdded = null;
        }

        try {
            if (durationStr != null) {
                String numbers = durationStr.replaceAll("[^0-9]", "");
                this.duration = Integer.parseInt(numbers);
            }
        } catch (Exception e) {
            this.duration = 0;
        }
    }

    @Override
    public int compareTo(NetflixShow other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    public String getShowId() { return showId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public int getDuration() { return duration; }
    public String getRating() { return rating; }
    public int getReleaseYear() { return releaseYear; }
    
    public int getAddedYear() {
        return (dateAdded != null) ? dateAdded.getYear() : -1;
    }

    @Override
    public String toString() {
        String dateStr = (dateAdded != null) ? dateAdded.toString() : "N/A";
        String durUnit = type.equalsIgnoreCase("Movie") ? " min" : " Seasons";
        
        String displayTitle = title.length() > 30 ? title.substring(0, 27) + "..." : title;

        return String.format("%-6s | %-8s | %-30s | %-12s | %-5s | %s", 
            showId, type, displayTitle, dateStr, rating, duration + durUnit);
    }
}