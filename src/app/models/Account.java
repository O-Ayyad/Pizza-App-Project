package app.models;

import app.utils.HashUtil;

public abstract class Account {
    private String username;
    private String hashedPassword;

    public Account(String username, String password) {
        this.username = username;
        this.hashedPassword = HashUtil.hash(password);
    }

    public String getUsername() { return username; }

    public boolean verifyPassword(String password) {
        //TODO: SQL here
        return hashedPassword.equals(HashUtil.hash(password));
    }

    public void updatePassword(String newPassword) {
        //TODO: SQL here
        hashedPassword = HashUtil.hash(newPassword);
    }
}
