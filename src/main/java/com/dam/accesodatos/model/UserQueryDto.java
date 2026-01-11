package com.dam.accesodatos.model;

public class UserQueryDto {

    private String name;
    private String department;
    private Boolean active;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

    public UserQueryDto() {
        this.page = 0;
        this.size = 10;
        this.sortBy = "name";
        this.sortDirection = "ASC";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page != null ? page : 0;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size != null ? size : 10;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "name";
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection != null ? sortDirection : "ASC";
    }

    public int getOffset() {
        return page * size;
    }

    @Override
    public String toString() {
        return "UserQueryDto{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", active=" + active +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
