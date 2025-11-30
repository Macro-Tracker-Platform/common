package com.olehprukhnytskyi.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class FaviconController {
    @GetMapping("favicon.ico")
    public ResponseEntity<Void> returnNoFavicon() {
        return ResponseEntity.ok(null);
    }
}
