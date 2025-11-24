import java.io.Serializable;
import java.time.LocalDate;

public class Expense implements Serializable {
    private String description;
    private double amount;
    private String category;
    private LocalDate date;
    private boolean isOnline; 

    public Expense(String description, double amount, String category, LocalDate date, boolean isOnline) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.isOnline = isOnline;
    }

    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public boolean isOnline() { return isOnline; } 

    @Override
    public String toString() {
        return date + " | " + category + " |  " + amount;
    }
}