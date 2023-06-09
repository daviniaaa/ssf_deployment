package ssf_assessment.model;

import jakarta.validation.constraints.Size;

public class User {
    @Size(min = 2, message = "Username must be at least 2 characters")
    private String username;

    @Size(min = 2, message = "Password must be at least 2 characters")
    private String password;
    
    public User() {
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    
    
}
