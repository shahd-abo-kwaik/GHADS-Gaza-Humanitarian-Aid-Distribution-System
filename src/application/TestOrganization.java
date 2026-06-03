package application;

import dao.OrganizationDAO;
import models.Organization;

public class TestOrganization {

    public static void main(String[] args) {

        Organization organization =
                new Organization("UNRWA", "NGO", "info@unrwa.org");

        OrganizationDAO dao = new OrganizationDAO();

        boolean result = dao.addOrganization(organization);

        if (result) {
            System.out.println("Organization Added Successfully!");
        } else {
            System.out.println("Failed To Add Organization!");
        }
    }
}