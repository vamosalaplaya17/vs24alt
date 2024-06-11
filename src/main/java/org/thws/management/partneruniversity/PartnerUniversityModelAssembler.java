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

/**
 * Assembler class, to convert PartnerUniversities into their model representation, named PartnerUniversityModel
 * Creates related links for each PartnerUniversity
 */
@Component
public class PartnerUniversityModelAssembler extends RepresentationModelAssemblerSupport<PartnerUniversity, PartnerUniversityModel> {

    private final UniModuleModelAssembler uniModuleModelAssembler;

    /**
     * Constructs a new PartnerUniversityModelAssembler
     *
     * @param uniModuleModelAssembler The assembler for PartnerUniversity entities
     */
    @Autowired
    public PartnerUniversityModelAssembler(UniModuleModelAssembler uniModuleModelAssembler) {
        super(PartnerUniversityController.class, PartnerUniversityModel.class);
        this.uniModuleModelAssembler = uniModuleModelAssembler;
    }

    /**
     * Converts given PartnerUniversity into a model representation
     *
     * @param partnerUniversity PartnerUniversity to convert
     * @return Converted model with related HATEOAS links
     */
    @Override
    @NonNull
    public PartnerUniversityModel toModel(@NonNull PartnerUniversity partnerUniversity) {
        PartnerUniversityModel partnerUniversityModel = convertToModel(partnerUniversity);

        partnerUniversityModel.add(linkTo(methodOn(PartnerUniversityController.class).getPartnerUniversity(partnerUniversityModel.getId())).withSelfRel().withType("GET"));
        partnerUniversityModel.add(linkTo(methodOn(PartnerUniversityController.class).updatePartnerUniversity(partnerUniversityModel.getId(), null)).withRel("update").withType("PUT"));
        partnerUniversityModel.add(linkTo(methodOn(PartnerUniversityController.class).deletePartnerUniversity(partnerUniversityModel.getId())).withRel("delete").withType("DELETE"));

        return partnerUniversityModel;
    }

    /**
     * Converts given PartnerUniversity into a model representation
     * This method is setting all the properties
     *
     * @param partnerUniversity PartnerUniversity to be converted
     * @return The converted model with set properties
     */
    private PartnerUniversityModel convertToModel(PartnerUniversity partnerUniversity) {
        PartnerUniversityModel partnerUniversityModel = instantiateModel(partnerUniversity);

        partnerUniversityModel.setId(partnerUniversity.getId());
        partnerUniversityModel.setName(partnerUniversity.getName());
        partnerUniversityModel.setCountry(partnerUniversity.getCountry());
        partnerUniversityModel.setDepartmentName(partnerUniversity.getDepartmentName());
        partnerUniversityModel.setDepartmentUrl(partnerUniversity.getDepartmentUrl());
        partnerUniversityModel.setContactPerson(partnerUniversity.getContactPerson());
        partnerUniversityModel.setMaxStudentsIn(partnerUniversity.getMaxStudentsIn());
        partnerUniversityModel.setMaxStudentsOut(partnerUniversity.getMaxStudentsOut());
        partnerUniversityModel.setNextSpringSemester(partnerUniversity.getNextSpringSemester());
        partnerUniversityModel.setNextSummerSemester(partnerUniversity.getNextSummerSemester());

        if (partnerUniversity.getModules() != null) {
            List<UniModuleModel> uniModuleModels = partnerUniversity.getModules().stream()
                    .map(uniModuleModelAssembler::toModel)
                    .collect(Collectors.toList());
            partnerUniversityModel.setUniModuleModels(uniModuleModels);
        }

        return partnerUniversityModel;
    }
}