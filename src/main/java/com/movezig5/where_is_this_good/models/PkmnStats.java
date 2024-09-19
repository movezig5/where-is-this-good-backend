package com.movezig5.where_is_this_good.models;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PkmnStats {
	Integer battles;
	Map<String, UsageStatistics> pokemon;
	String date;
	
	public UsageStatistics getStatsFor(String pokemonName) {
		return pokemon.get(pokemonName);
	}
}
