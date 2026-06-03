package dao;

import java.util.List;

import models.AidDistribution;

public class AidDistributionDAO {

	public boolean addDistribution(AidDistribution distribution) {

	    String sql =
	            "INSERT INTO aid_distributions "
	            + "(family_id, org_id, distributed_by, distribution_date, aid_type) "
	            + "VALUES (?, ?, ?, ?, ?)";

	    try {

	        java.sql.Connection connection =
	                config.DBConnection.getConnection();

	        java.sql.PreparedStatement statement =
	                connection.prepareStatement(sql);

	        statement.setInt(1, distribution.getFamilyId());
	        statement.setInt(2, distribution.getOrgId());
	        statement.setInt(3, distribution.getDistributedBy());

	        statement.setDate(
	                4,
	                java.sql.Date.valueOf(
	                        distribution.getDistributionDate()
	                )
	        );

	        statement.setString(
	                5,
	                distribution.getAidType()
	        );

	        int rowsInserted =
	                statement.executeUpdate();

	        return rowsInserted > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	public List<AidDistribution> getAllDistributions() {

	    List<AidDistribution> distributions = new java.util.ArrayList<>();

	    String sql =
	            "SELECT ad.*, f.household_name AS family_name, "
	            + "o.name AS organization_name, u.full_name AS coordinator_name "
	            + "FROM aid_distributions ad "
	            + "JOIN families f ON ad.family_id = f.family_id "
	            + "JOIN organizations o ON ad.org_id = o.org_id "
	            + "JOIN users u ON ad.distributed_by = u.user_id";

	    try {
	        java.sql.Connection connection =
	                config.DBConnection.getConnection();

	        java.sql.PreparedStatement statement =
	                connection.prepareStatement(sql);

	        java.sql.ResultSet resultSet =
	                statement.executeQuery();

	        while (resultSet.next()) {

	            AidDistribution distribution = new AidDistribution(
	                    resultSet.getInt("distribution_id"),
	                    resultSet.getInt("family_id"),
	                    resultSet.getString("family_name"),
	                    resultSet.getInt("org_id"),
	                    resultSet.getString("organization_name"),
	                    resultSet.getInt("distributed_by"),
	                    resultSet.getString("coordinator_name"),
	                    resultSet.getDate("distribution_date").toLocalDate(),
	                    resultSet.getString("aid_type")
	            );

	            distributions.add(distribution);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return distributions;
	}
   
	public boolean deleteDistribution(int distributionId) {

	    String sql = "DELETE FROM aid_distributions WHERE distribution_id = ?";

	    try {
	        java.sql.Connection connection =
	                config.DBConnection.getConnection();

	        java.sql.PreparedStatement statement =
	                connection.prepareStatement(sql);

	        statement.setInt(1, distributionId);

	        int rowsDeleted = statement.executeUpdate();

	        return rowsDeleted > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public boolean hasReceivedAidWithin30Days(int familyId) {

	    String sql =
	            "SELECT * FROM aid_distributions "
	            + "WHERE family_id = ? "
	            + "AND distribution_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";

	    try {

	        java.sql.Connection connection =
	                config.DBConnection.getConnection();

	        java.sql.PreparedStatement statement =
	                connection.prepareStatement(sql);

	        statement.setInt(1, familyId);

	        java.sql.ResultSet resultSet =
	                statement.executeQuery();

	        return resultSet.next();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
    
	public int getServedFamiliesCount() {

        String sql = "SELECT COUNT(DISTINCT family_id) AS total FROM aid_distributions";

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

    public int getNotServedFamiliesCount() {

        String sql =
                "SELECT COUNT(*) AS total FROM families "
                + "WHERE family_id NOT IN "
                + "(SELECT DISTINCT family_id FROM aid_distributions)";

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
    
    public List<AidDistribution> getDistributionsByOrganization(int orgId) {

        List<AidDistribution> distributions = new java.util.ArrayList<>();

        String sql =
                "SELECT ad.*, f.household_name AS family_name, "
                + "o.name AS organization_name, u.full_name AS coordinator_name "
                + "FROM aid_distributions ad "
                + "JOIN families f ON ad.family_id = f.family_id "
                + "JOIN organizations o ON ad.org_id = o.org_id "
                + "JOIN users u ON ad.distributed_by = u.user_id "
                + "WHERE ad.org_id = ?";

        try {
            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, orgId);

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            while (resultSet.next()) {

                AidDistribution distribution = new AidDistribution(
                        resultSet.getInt("distribution_id"),
                        resultSet.getInt("family_id"),
                        resultSet.getString("family_name"),
                        resultSet.getInt("org_id"),
                        resultSet.getString("organization_name"),
                        resultSet.getInt("distributed_by"),
                        resultSet.getString("coordinator_name"),
                        resultSet.getDate("distribution_date").toLocalDate(),
                        resultSet.getString("aid_type")
                );

                distributions.add(distribution);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return distributions;
    }
    
    public int getServedFamiliesCountByOrganization(int orgId) {

        String sql =
                "SELECT COUNT(DISTINCT family_id) AS total "
                + "FROM aid_distributions "
                + "WHERE org_id = ?";

        try {
            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, orgId);

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
    
    public AidDistribution getLastAidWithin30Days(int familyId) {

        String sql =
                "SELECT ad.*, f.household_name AS family_name, "
                + "o.name AS organization_name, u.full_name AS coordinator_name "
                + "FROM aid_distributions ad "
                + "JOIN families f ON ad.family_id = f.family_id "
                + "JOIN organizations o ON ad.org_id = o.org_id "
                + "JOIN users u ON ad.distributed_by = u.user_id "
                + "WHERE ad.family_id = ? "
                + "AND ad.distribution_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) "
                + "ORDER BY ad.distribution_date DESC "
                + "LIMIT 1";

        try {
            java.sql.Connection connection =
                    config.DBConnection.getConnection();

            java.sql.PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, familyId);

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {
                return new AidDistribution(
                        resultSet.getInt("distribution_id"),
                        resultSet.getInt("family_id"),
                        resultSet.getString("family_name"),
                        resultSet.getInt("org_id"),
                        resultSet.getString("organization_name"),
                        resultSet.getInt("distributed_by"),
                        resultSet.getString("coordinator_name"),
                        resultSet.getDate("distribution_date").toLocalDate(),
                        resultSet.getString("aid_type")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}