package org.thws.management.unimodule;

import jakarta.persistence.*;
import org.thws.management.partneruniversity.PartnerUniversity;

@Entity
@Table(name = "uni_modules")
public class UniModule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unimodule_sequence")
    @SequenceGenerator(name = "unimodule_sequence", sequenceName = "unimodule_sequence", allocationSize = 1)
    private Long id;

    private String name;
    private Integer semester;
    private Integer ects;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_university_id")
    private PartnerUniversity partnerUniversity;

    public UniModule() {
    }

    public UniModule(String name, int semester, int ects, PartnerUniversity partnerUniversity) {
        this.name = name;
        this.semester = semester;
        this.ects = ects;
        this.partnerUniversity = partnerUniversity;
    }

    public UniModule(Long id, String name, Integer semester, Integer ects, PartnerUniversity partnerUniversity) {
        this.id = id;
        this.name = name;
        this.semester = semester;
        this.ects = ects;
        this.partnerUniversity = partnerUniversity;
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

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Integer getEcts() {
        return ects;
    }

    public void setEcts(int ects) {
        this.ects = ects;
    }

    public PartnerUniversity getPartnerUniversity() {
        return partnerUniversity;
    }

    public void setPartnerUniversity(PartnerUniversity partnerUniversity) {
        this.partnerUniversity = partnerUniversity;
    }
}
