
package dao;

import java.util.List;

import models.Family;
import java.time.LocalDate;
public class FamilyDAO {
	public boolean addFamily(Family family) {

	    String sql = "INSERT INTO families "
	            + "(household_name, phone, location, family_size, national_id, "
	            + "vulnerability_level, registration_date, last_aid_date) "
	            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	    try {

	        java.sql.Connection connection =
	                config.DBConnection.getConnection();

	        java.sql.PreparedStatement statement =
	                connection.prepareStatement(sql);

	        statement.setString(1, family.getHouseholdName());
	        statement.setString(2, family.getPhone());
	        statement.setString(3, family.getLocation());
	        statement.setInt(4, family.getFamilySize());
	        statement.setString(5, family.getNationalId());
	        statement.setString(6, family.getVulnerabilityLevel());
	        statement.setDate(7,
	                java.sql.Date.valueOf(
	                        family.getRegistrationDate()));
	        if (family.getLastAidDate() == null) {
	            statement.setDate(8, null);
	        } else {
	            statement.setDate(8, java.sql.Date.valueOf(family.getLastAidDate()));
	        }
	        int rowsInserted = statement.executeUpdate();

	        return rowsInserted > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean updateFamily(Family family) {

	    String sql = "UPDATE families SET household_name=?, phone=?, location=?, family_size=?, "
	            + "national_id=?, vulnerability_level=?, registration_date=?, last_aid_date=? "
	            + "WHERE family_id=?";

	    try {
	        java.sql.Connection connection =
	                config.DBConnection.getConnection();

	        java.sql.PreparedStatement statement =
	                connection.prepareStatement(sql);

	        statement.setString(1, family.getHouseholdName());
	        statement.setString(2, family.getPhone());
	        statement.setString(3, family.getLocation());
	        statement.setInt(4, family.getFamilySize());
	        statement.setString(5, family.getNationalId());
	        statement.setString(6, family.getVulnerabilityLevel());
	        statement.setDate(7, java.sql.Date.valueOf(family.getRegistrationDate()));

	        if (family.getLastAidDate() == null) {
	            statement.setDate(8, null);
	        } else {
	            statement.setDate(8, java.sql.Date.valueOf(family.getLastAidDate()));
	        }

	        statement.setInt(9, family.getFamilyId());

	        int rowsUpdated = statement.executeUpdate();

	        return rowsUpdated > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public boolean deleteFamily(int familyId) {

	    String sql = "DELETE FROM families WHERE family_id = ?";

	    try {
	        java.sql.Connection connection =
	                config.DBConnection.getConnection();

	        java.sql.PreparedStatement statement =
	                connection.prepareStatement(sql);

	        statement.setInt(1, familyId);

	        int rowsDeleted = statement.executeUpdate();

	        return rowsDeleted > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
    public List<Family> getAllFamilies() {

        List<Family> families = new java.util.ArrayList<>();

        String sql = "SELECT * FROM families";

        try {
            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                Family family = new Family(
                        resultSet.getInt("family_id"),
                        resultSet.getString("household_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("location"),
                        resultSet.getInt("family_size"),
                        resultSet.getString("national_id"),
                        resultSet.getString("vulnerability_level"),
                        resultSet.getDate("registration_date").toLocalDate(),
                        resultSet.getDate("last_aid_date") == null
                                ? null
                                : resultSet.getDate("last_aid_date").toLocalDate()
                );

                families.add(family);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return families;
    }
    public boolean nationalIdExists(String nationalId) {

        String sql =
                "SELECT * FROM families WHERE national_id = ?";

        try {

            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, nationalId);

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            return resultSet.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getFamiliesCount() {

        String sql = "SELECT COUNT(*) AS total FROM families";

        try {
            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    public List<Family> getFamiliesSortedByVulnerability() {

        List<Family> families = new java.util.ArrayList<>();

        String sql =
                "SELECT * FROM families "
                + "ORDER BY CASE vulnerability_level "
                + "WHEN 'HIGH' THEN 1 "
                + "WHEN 'MEDIUM' THEN 2 "
                + "WHEN 'LOW' THEN 3 "
                + "ELSE 4 END";

        try {
            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {
                Family family = new Family(
                        resultSet.getInt("family_id"),
                        resultSet.getString("household_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("location"),
                        resultSet.getInt("family_size"),
                        resultSet.getString("national_id"),
                        resultSet.getString("vulnerability_level"),
                        resultSet.getDate("registration_date").toLocalDate(),
                        resultSet.getDate("last_aid_date") == null
                                ? null
                                : resultSet.getDate("last_aid_date").toLocalDate()
                );

                families.add(family);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return families;
    }
    
    public List<Family> getNotServedFamilies() {

        List<Family> families = new java.util.ArrayList<>();

        String sql =
                "SELECT * FROM families "
                + "WHERE family_id NOT IN "
                + "(SELECT DISTINCT family_id FROM aid_distributions)";

        try {
            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {
                Family family = new Family(
                        resultSet.getInt("family_id"),
                        resultSet.getString("household_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("location"),
                        resultSet.getInt("family_size"),
                        resultSet.getString("national_id"),
                        resultSet.getString("vulnerability_level"),
                        resultSet.getDate("registration_date").toLocalDate(),
                        resultSet.getDate("last_aid_date") == null
                                ? null
                                : resultSet.getDate("last_aid_date").toLocalDate()
                );

                families.add(family);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return families;
    }
    
    public boolean updateLastAidDate(int familyId, LocalDate date) {

        String sql =
                "UPDATE families SET last_aid_date = ? WHERE family_id = ?";

        try {

            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setDate(1, java.sql.Date.valueOf(date));
            statement.setInt(2, familyId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}