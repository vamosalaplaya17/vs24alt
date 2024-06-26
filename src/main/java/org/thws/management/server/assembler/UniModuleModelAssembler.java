package org.thws.management.server.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.thws.management.server.controller.UniModuleController;
import org.thws.management.server.model.UniModule;
import org.thws.management.server.model.UniModuleModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler class, to convert UniModules into their model representation, named UniModuleModel
 */
@Component
public class UniModuleModelAssembler extends RepresentationModelAssemblerSupport<UniModule, UniModuleModel> {

    /**
     * Constructs the assembler
     */
    public UniModuleModelAssembler() {
        super(UniModuleController.class, UniModuleModel.class);
    }

    /**
     * Converts UniModule to UniModuleModel
     *
     * @param uniModule The UniModule to convert
     * @return Representation of UniModule as UniModuleModel with self link
     */
    @Override
    @NonNull
    public UniModuleModel toModel(@NonNull UniModule uniModule) {
        UniModuleModel model = instantiateModel(uniModule);

        model.setId(uniModule.getId());
        model.setName(uniModule.getName());
        model.setSemester(uniModule.getSemester());
        model.setEcts(uniModule.getEcts());

        model.add(linkTo(methodOn(UniModuleController.class).getUniModule(uniModule.getPartnerUniversity().getId(), uniModule.getId())).withSelfRel().withType("GET"));

        return model;
    }
}
