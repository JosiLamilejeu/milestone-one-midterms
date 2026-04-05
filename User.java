public class User {
    private final String username;
    private final String password;
    private final String role;
    private final String rank;
    private final String race;
    private final String userClass;
    
    public User(String username, String password, String role, String rank, String race, String userClass) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.rank = rank;
        this.race = race;
        this.userClass = userClass;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getRole() {
        return role;
    }
    
    public String getRank() {
        return rank;
    }
    
    public String getRace() {
        return race;
    }
    
    public String getUserClass() {
        return userClass;
    }
    
}