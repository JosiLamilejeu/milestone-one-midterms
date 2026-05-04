public class User {
    private final String USERNAME;
    private final String PASSWORD;
    private final String ROLE;

    private User(UserBuilder builder) {
        this.USERNAME = builder.username;
        this.PASSWORD = builder.password;
        this.ROLE = builder.role;
    }
    
    public String getUsername() {
        return USERNAME;
    }

    public String getPassword() {
        return PASSWORD;
    }

    public String getRole() {
        return ROLE;
    }

    public static class UserBuilder {
        private String username;
        private String password;
        private String role;

        public UserBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder setRole(String role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
