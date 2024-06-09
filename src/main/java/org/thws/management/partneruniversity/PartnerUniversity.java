package org.thws.management.partneruniversity;

import jakarta.persistence.*;
import org.thws.management.unimodule.UniModule;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table
public class PartnerUniversity {
    @Id
    @SequenceGenerator(
            name = "partneruniversity_sequence",
            sequenceName = "partneruniversity_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "partneruniversity_sequence"
    )
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

    @OneToMany(mappedBy = "partnerUniversity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UniModule> modules;

    public PartnerUniversity() {
    }

    public PartnerUniversity(Long id, String name, String country, String departmentName, String departmentUrl,
                             String contactPerson, Integer maxStudentsIn, Integer maxStudentsOut,
                             LocalDate nextSpringSemester, LocalDate nextSummerSemester) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.departmentName = departmentName;
        this.departmentUrl = departmentUrl;
        this.contactPerson = contactPerson;
        this.maxStudentsIn = maxStudentsIn;
        this.maxStudentsOut = maxStudentsOut;
        this.nextSpringSemester = nextSpringSemester;
        this.nextSummerSemester = nextSummerSemester;
    }

    public PartnerUniversity(String name, String country, String departmentName, String departmentUrl,
                             String contactPerson, Integer maxStudentsIn, Integer maxStudentsOut,
                             LocalDate nextSpringSemester, LocalDate nextSummerSemester) {
        this.name = name;
        this.country = country;
        this.departmentName = departmentName;
        this.departmentUrl = departmentUrl;
        this.contactPerson = contactPerson;
        this.maxStudentsIn = maxStudentsIn;
        this.maxStudentsOut = maxStudentsOut;
        this.nextSpringSemester = nextSpringSemester;
        this.nextSummerSemester = nextSummerSemester;
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

    public List<UniModule> getModules() {
        return modules;
    }

    public void setModules(List<UniModule> modules) {
        this.modules = modules;
    }

    @Override
    public String toString() {
        return "PartnerUniversity{" +
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
                '}';
    }
}
