package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DBConnection;
import models.User;

public class UserDAO {

    public boolean login(String username, String password, String role) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(User user) {

        String sql = "INSERT INTO users (username, password, full_name, email, role, org_id, photo_path) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getRole());
            statement.setInt(6, user.getOrgId());
            statement.setString(7, user.getPhotoPath());

            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<User> getAllUsers() {

        ArrayList<User> users = new ArrayList<>();

        String sql = "SELECT u.*, o.name AS organization_name "
                + "FROM users u "
                + "LEFT JOIN organizations o ON u.org_id = o.org_id";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                User user = new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        resultSet.getString("role"),
                        resultSet.getInt("org_id"),
                        resultSet.getString("organization_name"),
                        resultSet.getString("photo_path")
                );

                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public boolean usernameExists(String username) {

        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {

        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateUser(User user) {

        String sql = "UPDATE users SET username=?, password=?, full_name=?, email=?, role=?, org_id=?, photo_path=? WHERE user_id=?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getRole());
            statement.setInt(6, user.getOrgId());
            statement.setString(7, user.getPhotoPath());
            statement.setInt(8, user.getUserId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {

        String sql = "DELETE FROM users WHERE user_id = ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean changePassword(String username, String currentPassword, String newPassword) {

        String sql = "UPDATE users SET password = ? WHERE username = ? AND password = ?";

        try {
            java.sql.Connection connection = config.DBConnection.getConnection();
            java.sql.PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, newPassword);
            statement.setString(2, username);
            statement.setString(3, currentPassword);

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getCoordinatorsCount() {

        String sql = "SELECT COUNT(*) AS total FROM users WHERE role = 'COORDINATOR'";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public User getUserByLogin(String username, String password, String role) {

        String sql = "SELECT u.*, o.name AS organization_name "
                + "FROM users u "
                + "LEFT JOIN organizations o ON u.org_id = o.org_id "
                + "WHERE u.username = ? AND u.password = ? AND u.role = ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        resultSet.getString("role"),
                        resultSet.getInt("org_id"),
                        resultSet.getString("organization_name"),
                        resultSet.getString("photo_path")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}