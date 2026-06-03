package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import config.DBConnection;
import models.Organization;

public class OrganizationDAO {

    public boolean addOrganization(Organization organization) {

        String sql = "INSERT INTO organizations (name, type, contact_info) VALUES (?, ?, ?)";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, organization.getName());
            statement.setString(2, organization.getType());
            statement.setString(3, organization.getContactInfo());

            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Organization> getAllOrganizations() {

        ArrayList<Organization> organizations = new ArrayList<>();

        String sql = "SELECT * FROM organizations";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Organization organization = new Organization(
                        resultSet.getInt("org_id"),
                        resultSet.getString("name"),
                        resultSet.getString("type"),
                        resultSet.getString("contact_info")
                );

                organizations.add(organization);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return organizations;
    }
    
    public boolean updateOrganization(Organization organization) {

        String sql = "UPDATE organizations SET name = ?, type = ?, contact_info = ? WHERE org_id = ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, organization.getName());
            statement.setString(2, organization.getType());
            statement.setString(3, organization.getContactInfo());
            statement.setInt(4, organization.getOrgId());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteOrganization(int orgId) {

        String sql = "DELETE FROM organizations WHERE org_id = ?";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, orgId);

            int rowsDeleted = statement.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }public boolean organizationNameExists(String name) {

        String sql = "SELECT * FROM organizations WHERE LOWER(name) = LOWER(?)";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getOrganizationsCount() {

        String sql = "SELECT COUNT(*) AS total FROM organizations";

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
}