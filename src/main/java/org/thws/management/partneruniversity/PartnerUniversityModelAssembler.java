package org.thws.management.partneruniversity;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PartnerUniversityModelAssembler extends RepresentationModelAssemblerSupport<PartnerUniversity, PartnerUniversityModel> {

    public PartnerUniversityModelAssembler() {
        super(PartnerUniversityController.class, PartnerUniversityModel.class);
    }

    @Override
    @NonNull
    public PartnerUniversityModel toModel(@NonNull PartnerUniversity partnerUniversity) {
        PartnerUniversityModel model = instantiateModel(partnerUniversity);

        model.add(linkTo(methodOn(PartnerUniversityController.class)
                .getPartnerUniversity(partnerUniversity.getId())).withSelfRel().withRel("GET"));

        model.setId(partnerUniversity.getId());
        model.setName(partnerUniversity.getName());
        model.setCountry(partnerUniversity.getCountry());
        model.setDepartmentName(partnerUniversity.getDepartmentName());
        model.setDepartmentUrl(partnerUniversity.getDepartmentUrl());
        model.setContactPerson(partnerUniversity.getContactPerson());
        model.setMaxStudentsIn(partnerUniversity.getMaxStudentsIn());
        model.setMaxStudentsOut(partnerUniversity.getMaxStudentsOut());
        model.setNextSpringSemester(partnerUniversity.getNextSpringSemester());
        model.setNextSummerSemester(partnerUniversity.getNextSummerSemester());

        return model;
    }
}