package org.thws.management.unimodule;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UniModuleModelAssembler extends RepresentationModelAssemblerSupport<UniModule, UniModuleModel> {

    public UniModuleModelAssembler() {
        super(UniModuleController.class, UniModuleModel.class);
    }

    @Override
    @NonNull
    public UniModuleModel toModel(@NonNull UniModule uniModule) {
        UniModuleModel model = instantiateModel(uniModule);

        model.setId(uniModule.getId());
        model.setName(uniModule.getName());
        model.setSemester(uniModule.getSemester());
        model.setEcts(uniModule.getEcts());

        model.add(linkTo(methodOn(UniModuleController.class).getUniModule(uniModule.getPartnerUniversity().getId(), uniModule.getId())).withSelfRel());
        model.add(linkTo(methodOn(UniModuleController.class).updateUniModel(uniModule.getPartnerUniversity().getId(), uniModule.getId(), null)).withRel("update").withType("PUT"));
        model.add(linkTo(methodOn(UniModuleController.class).deleteUniModule(uniModule.getPartnerUniversity().getId(), uniModule.getId())).withRel("delete").withType("DELETE"));

        return model;
    }
}
