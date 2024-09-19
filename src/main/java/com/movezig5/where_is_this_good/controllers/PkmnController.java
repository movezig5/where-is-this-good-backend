package com.movezig5.where_is_this_good.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.movezig5.where_is_this_good.services.PkmnService;

@RestController
public class PkmnController {
	@Autowired
	private PkmnService pkmnService;
	
	@GetMapping("/health-check")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("The application is up and running.");
	}
	
	@GetMapping("/best-formats/{pokemon}")
	public ResponseEntity<String> bestFormats(@PathVariable String pokemon) {
		return ResponseEntity.ok(pkmnService.getUsageAsJson(StringUtils.capitalize(pokemon), 0.2f));
	}
}
