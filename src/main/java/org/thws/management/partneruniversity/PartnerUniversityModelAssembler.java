package org.thws.management.partneruniversity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.thws.management.unimodule.UniModuleModel;
import org.thws.management.unimodule.UniModuleModelAssembler;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PartnerUniversityModelAssembler extends RepresentationModelAssemblerSupport<PartnerUniversity, PartnerUniversityModel> {

    @Autowired
    private UniModuleModelAssembler uniModuleModelAssembler;

    public PartnerUniversityModelAssembler() {
        super(PartnerUniversityController.class, PartnerUniversityModel.class);
    }

    @Override
    @NonNull
    public PartnerUniversityModel toModel(@NonNull PartnerUniversity partnerUniversity) {
        PartnerUniversityModel model = instantiateModel(partnerUniversity);

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

        List<UniModuleModel> uniModuleModels = partnerUniversity.getModules().stream()
                .map(uniModuleModelAssembler::toModel)
                .collect(Collectors.toList());
        model.setUniModuleModels(uniModuleModels);

        model.add(linkTo(methodOn(PartnerUniversityController.class)
                .getPartnerUniversity(partnerUniversity.getId())).withSelfRel());

        model.add(linkTo(methodOn(PartnerUniversityController.class)
                .updatePartnerUniversity(partnerUniversity.getId(), null)).withRel("update").withType("PUT"));

        model.add(linkTo(methodOn(PartnerUniversityController.class)
                .deletePartnerUniversity(partnerUniversity.getId())).withRel("delete").withType("DELETE"));

        return model;
    }
}