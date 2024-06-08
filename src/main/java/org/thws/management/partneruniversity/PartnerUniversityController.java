package org.thws.management.partneruniversity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String departmentName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PartnerUniversity> partnerUniversities;

        if (name != null && country != null && departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByNameAndCountryAndDepartmentName(
                    name.toLowerCase(), country.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (name != null && country != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByNameAndCountry(
                    name.toLowerCase(), country.toLowerCase(), pageable);
        } else if (name != null && departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByNameAndDepartmentName(
                    name.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (country != null && departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByCountryAndDepartmentName(
                    country.toLowerCase(), departmentName.toLowerCase(), pageable);
        } else if (name != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByName(
                    name.toLowerCase(), pageable);
        } else if (country != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByCountry(
                    country.toLowerCase(), pageable);
        } else if (departmentName != null) {
            partnerUniversities = partnerUniversityService.getPartnerUniversitiesByDepartmentName(
                    departmentName.toLowerCase(), pageable);
        } else {
            partnerUniversities = partnerUniversityService.getAllPartnerUniversities(pageable);
        }

        return ResponseEntity.ok(partnerUniversities);
    }

        @GetMapping(path = "{partneruniversityId}")
        public ResponseEntity<PartnerUniversity> getPartnerUniversity (@PathVariable("partneruniversityId") Long
        partnerUniversityId){
            PartnerUniversity partnerUniversity = partnerUniversityService.getPartnerUniversityById(partnerUniversityId);

            if (partnerUniversity == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(partnerUniversity);
            }
        }

        @PostMapping
        public void addNewPartnerUniversity (@RequestBody PartnerUniversity partnerUniversity){
            partnerUniversityService.addNewPartnerUniversity(partnerUniversity);
        }

        @DeleteMapping(path = "{partneruniversityId}")
        public void deletePartnerUniversity (@PathVariable("partneruniversityId") Long partneruniversityId){
            partnerUniversityService.deletePartnerUniversity(partneruniversityId);
        }

        @PutMapping(path = "{partneruniversityId}")
        public void updatePartnerUniversity (@PathVariable("partneruniversityId") Long partneruniversityId,
                @RequestBody PartnerUniversity partnerUniversity){
            partnerUniversityService.updatePartnerUniversity(partneruniversityId, partnerUniversity);
        }
    }
