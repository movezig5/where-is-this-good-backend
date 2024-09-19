package com.movezig5.where_is_this_good.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movezig5.where_is_this_good.models.PkmnStats;

@Service
public class PkmnService {
	// Base URL for pkmn's Smogon API
	private final String baseUrl = "https://pkmn.github.io/smogon/data";
	
	// Retrieve stats for a given format
	public PkmnStats getStats(String format) {
		RestClient restClient = RestClient.create(baseUrl);
		return restClient.get()
				.uri("/stats/" + format)
				.retrieve()
				.body(PkmnStats.class);
	}
	
	// Get a list of all formats that there is data available for
	public List<String> getFormats() {
		RestClient restClient = RestClient.create(baseUrl);
		Map<String, List<Integer>> formatMap = restClient.get()
				.uri("/stats/index.json")
				.retrieve()
				.body(new ParameterizedTypeReference<Map<String, List<Integer>>>() {});
		return formatMap.keySet().stream().collect(Collectors.toList());
	}
	
	// Get all percent usages greater than zero for a Pokemon in all formats
	public Map<String, Float> getUsageInFormats(String pokemon) throws ResponseStatusException {
		return getUsageInFormats(pokemon, 0.0f);
	}
	
	// Get all percent usages greaer than a provided value for a Pokemon in all formats
	public Map<String, Float> getUsageInFormats(String pokemon, float cutoff) throws ResponseStatusException {
		List<String> formats = getFormats();
		Map<String, Float> usages = new HashMap<>();
		
		for(String format : formats) {
			PkmnStats stats = getStats(format);
			if (stats.getPokemon().containsKey(pokemon)) {
				usages.put(format.substring(0, format.length() - 5), stats.getStatsFor(pokemon).getUsage().getWeighted());
			}
		}
		
		if (usages.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No status available for \"" + pokemon + "\"");
		}
		return filterResults(usages, cutoff);
	}
	
	// Filter the usage statistics for a given Pokemon to only return usage greater than a certain amount.
	// For example, if the cutoff is 0.5, the function will filter out all usages less than 50%.
	public Map<String, Float> filterResults(Map<String, Float> data, Float cutoff) {
		return data.entrySet().stream()
				.filter(e->e.getValue() >= cutoff)
				.collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
	}
	
	// Get all usage data for a Pokemon in all formats greater than a specified cutoff value, and return it as JSON.
	public String getUsageAsJson(String pokemon, Float cutoff) throws ResponseStatusException {
		try {
			return new ObjectMapper().writeValueAsString(getUsageInFormats(pokemon, cutoff));
		} catch (JsonProcessingException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing usage data as JSON");
		}
	}
}
