package com.notelify.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authenticated")
@PreAuthorize("isAuthenticated()")
public class AuthenticatedController {

    @GetMapping
    public String index() {
        return "index.html";
    }
}
