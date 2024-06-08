package org.thws.management.partneruniversity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/partner-universities")
public class PartnerUniversityController {

    private final PartnerUniversityService partnerUniversityService;

    @Autowired
    public PartnerUniversityController(PartnerUniversityService partnerUniversityService) {
        this.partnerUniversityService = partnerUniversityService;
    }

    @GetMapping
    public ResponseEntity<Page<PartnerUniversity>> getPartnerUniversities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PartnerUniversity> partnerUniversities = partnerUniversityService.getAllPartnerUniversities(pageable);

        return ResponseEntity.ok(partnerUniversities);
    }

    @GetMapping(path = "{partneruniversityId}")
    public ResponseEntity<PartnerUniversity> getPartnerUniversity(@PathVariable("partneruniversityId") Long partnerUniversityId) {
        PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);

        if (partnerUniversity == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(partnerUniversity);
        }
    }

    @PostMapping
    public void addNewPartnerUniversity(@RequestBody PartnerUniversity partnerUniversity) {
        partnerUniversityService.addNewPartnerUniversity(partnerUniversity);
    }

    @DeleteMapping(path = "{partneruniversityId}")
    public void deletePartnerUniversity(@PathVariable("partneruniversityId") Long partneruniversityId) {
        partnerUniversityService.deletePartnerUniversity(partneruniversityId);
    }

    @PutMapping(path = "{partneruniversityId}")
    public void updatePartnerUniversity(@PathVariable("partneruniversityId") Long partneruniversityId,
                                        @RequestBody PartnerUniversity partnerUniversity) {
        partnerUniversityService.updatePartnerUniversity(partneruniversityId, partnerUniversity);
    }
}
