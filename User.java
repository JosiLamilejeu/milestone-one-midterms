public class User {
    private final String username;
    private final String password;
    private final String role;

    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.role = builder.role;
    }

    public static class Builder {
        private String username;
        private String password;
        private String role;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(this);
        }
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
}
