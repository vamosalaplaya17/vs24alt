package org.thws.management.server.model;

import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.util.List;

/**
 * Representation model for a PartnerUniversity, containing only the basic information
 */
public class PartnerUniversityModel extends RepresentationModel<PartnerUniversityModel> {
    private Long id;
    private String name;
    private String country;
    private String departmentName;
    private String departmentUrl;
    private String contactPerson;
    private Integer maxStudentsIn;
    private Integer maxStudentsOut;
    private LocalDate nextSpringSemester;
    private LocalDate nextSummerSemester;
    private List<UniModuleModel> uniModuleModels;

    public PartnerUniversityModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentUrl() {
        return departmentUrl;
    }

    public void setDepartmentUrl(String departmentUrl) {
        this.departmentUrl = departmentUrl;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Integer getMaxStudentsIn() {
        return maxStudentsIn;
    }

    public void setMaxStudentsIn(Integer maxStudentsIn) {
        this.maxStudentsIn = maxStudentsIn;
    }

    public Integer getMaxStudentsOut() {
        return maxStudentsOut;
    }

    public void setMaxStudentsOut(Integer maxStudentsOut) {
        this.maxStudentsOut = maxStudentsOut;
    }

    public LocalDate getNextSpringSemester() {
        return nextSpringSemester;
    }

    public void setNextSpringSemester(LocalDate nextSpringSemester) {
        this.nextSpringSemester = nextSpringSemester;
    }

    public LocalDate getNextSummerSemester() {
        return nextSummerSemester;
    }

    public void setNextSummerSemester(LocalDate nextSummerSemester) {
        this.nextSummerSemester = nextSummerSemester;
    }

    public List<UniModuleModel> getUniModuleModels() {
        return uniModuleModels;
    }

    public void setUniModuleModels(List<UniModuleModel> uniModules) {
        this.uniModuleModels = uniModules;
    }

    @Override
    public String toString() {
        return "PartnerUniversityModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", departmentUrl='" + departmentUrl + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", maxStudentsIn=" + maxStudentsIn +
                ", maxStudentsOut=" + maxStudentsOut +
                ", nextSpringSemester=" + nextSpringSemester +
                ", nextSummerSemester=" + nextSummerSemester +
                ", uniModuleModels=" + uniModuleModels +
                '}';
    }
}