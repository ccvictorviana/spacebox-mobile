package br.com.spacebox.api.model.request;

import br.com.spacebox.utils.observer.IObservableEntity;

public class UserRequest implements IObservableEntity {

    private String id;
    private String username;
    private String name;
    private String email;
    private String password;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class Builder {

        private String username;
        private String name;
        private String email;
        private String password;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUserName(String username) {
            this.username = username;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserRequest build() {
            UserRequest request = new UserRequest();
            request.setName(name);
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);

            return request;
        }
    }
}