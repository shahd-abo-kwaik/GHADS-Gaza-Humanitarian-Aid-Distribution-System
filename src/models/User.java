package models;

public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
    private int orgId;
    private String organizationName;
    private String photoPath;

    public User() {
    }

    public User(int userId, String username, String password, String fullName,
                String email, String role, int orgId, String organizationName, String photoPath) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.orgId = orgId;
        this.organizationName = organizationName;
        this.photoPath = photoPath;
    }

    public User(String username, String password, String fullName,
                String email, String role, int orgId, String photoPath) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.orgId = orgId;
        this.photoPath = photoPath;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public int getOrgId() {
        return orgId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    @Override
    public String toString() {
        return fullName;
    }
}