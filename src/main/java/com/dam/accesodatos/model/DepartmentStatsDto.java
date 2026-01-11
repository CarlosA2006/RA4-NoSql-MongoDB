package com.dam.accesodatos.model;

/**
 * DTO para estadísticas de usuarios por departamento.
 * Usado por el aggregation pipeline de MongoDB.
 */
public class DepartmentStatsDto {

    private String department;
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;

    public DepartmentStatsDto() {
    }

    public DepartmentStatsDto(String department, long totalUsers, long activeUsers) {
        this.department = department;
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = totalUsers - activeUsers;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    @Override
    public String toString() {
        return "DepartmentStatsDto{" +
                "department='" + department + '\'' +
                ", totalUsers=" + totalUsers +
                ", activeUsers=" + activeUsers +
                ", inactiveUsers=" + inactiveUsers +
                '}';
    }
}
