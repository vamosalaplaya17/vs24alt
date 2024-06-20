package org.thws.management.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thws.management.server.service.DatabaseResetService;

/**
 * Controller class for resetting the database
 */
@RestController
@RequestMapping("/api/v1")
public class DatabaseResetController {

    private final DatabaseResetService databaseResetService;

    //constructor
    @Autowired
    public DatabaseResetController(DatabaseResetService databaseResetService) {
        this.databaseResetService = databaseResetService;
    }

    /**
     * Method to reset the database, executed by going to the URL /api/v1/reset-database
     *
     * @throws Exception when something goes wrong
     */
    @PostMapping("/reset-database")
    @ResponseStatus(HttpStatus.OK)
    public void resetDatabase() throws Exception {
        databaseResetService.resetDatabase();
    }
}
