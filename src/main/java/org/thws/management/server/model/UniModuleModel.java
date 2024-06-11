package org.thws.management.server.model;

import org.springframework.hateoas.RepresentationModel;

/**
 * Representation model for a UniModule, containing only the basic information
 */
public class UniModuleModel extends RepresentationModel<UniModuleModel> {
    private Long id;
    private String name;
    private Integer semester;
    private Integer ects;

    public UniModuleModel() {
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

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semesterOffered) {
        this.semester = semesterOffered;
    }

    public Integer getEcts() {
        return ects;
    }

    public void setEcts(Integer creditPoints) {
        this.ects = creditPoints;
    }
}
