import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private String password;
    private String name = "New User";
    private String email = "student@college.edu";
    private int age = 20;
    private String mobile = "N/A";
    private String profileImagePath = "";
    private double monthlyGoal = 500.00;
    private ArrayList<Expense> expenses = new ArrayList<>();
    
    private double onlineBalance = 0.00;
    private double offlineBalance = 0.00;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public String getMobile() { return mobile; }
    public String getProfileImagePath() { return profileImagePath; }
    public double getMonthlyGoal() { return monthlyGoal; }
    public ArrayList<Expense> getExpenses() { return expenses; }
    public double getOnlineBalance() { return onlineBalance; }
    public double getOfflineBalance() { return offlineBalance; }
    public double getTotalBalance() { return onlineBalance + offlineBalance; } // Calculated

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAge(int age) { this.age = age; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }
    public void setMonthlyGoal(double monthlyGoal) { this.monthlyGoal = monthlyGoal; }
    public void setPassword(String password) { this.password = password; } 
    public void setOnlineBalance(double onlineBalance) { this.onlineBalance = onlineBalance; }
    public void setOfflineBalance(double offlineBalance) { this.offlineBalance = offlineBalance; }
}