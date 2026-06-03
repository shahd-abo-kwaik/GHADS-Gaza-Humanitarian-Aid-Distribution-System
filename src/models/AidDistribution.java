package models;

import java.time.LocalDate;

public class AidDistribution {

    private int distributionId;
    private int familyId;
    private String familyName;
    private int orgId;
    private String organizationName;
    private int distributedBy;
    private String coordinatorName;
    private LocalDate distributionDate;
    private String aidType;

    public AidDistribution() {
    }

    public AidDistribution(int distributionId, int familyId, String familyName,
                           int orgId, String organizationName,
                           int distributedBy, String coordinatorName,
                           LocalDate distributionDate, String aidType) {
        this.distributionId = distributionId;
        this.familyId = familyId;
        this.familyName = familyName;
        this.orgId = orgId;
        this.organizationName = organizationName;
        this.distributedBy = distributedBy;
        this.coordinatorName = coordinatorName;
        this.distributionDate = distributionDate;
        this.aidType = aidType;
    }

    public AidDistribution(int familyId, int orgId, int distributedBy,
                           LocalDate distributionDate, String aidType) {
        this.familyId = familyId;
        this.orgId = orgId;
        this.distributedBy = distributedBy;
        this.distributionDate = distributionDate;
        this.aidType = aidType;
    }

    public int getDistributionId() {
        return distributionId;
    }

    public int getFamilyId() {
        return familyId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public int getOrgId() {
        return orgId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public int getDistributedBy() {
        return distributedBy;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public LocalDate getDistributionDate() {
        return distributionDate;
    }

    public String getAidType() {
        return aidType;
    }

    public void setDistributionId(int distributionId) {
        this.distributionId = distributionId;
    }

    public void setFamilyId(int familyId) {
        this.familyId = familyId;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public void setDistributedBy(int distributedBy) {
        this.distributedBy = distributedBy;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public void setDistributionDate(LocalDate distributionDate) {
        this.distributionDate = distributionDate;
    }

    public void setAidType(String aidType) {
        this.aidType = aidType;
    }
}