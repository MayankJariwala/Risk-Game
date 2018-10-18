package com.risk.model.gui;

import java.util.List;

/**
 * Model Class for Map.
 * 
 * @author <a href="mailto:l_grew@encs.concordia.ca">LoveshantGrewal</a>
 * @version 0.0.1
 */
public class Map {

	String currentMap;
	List<Continent> continents;
	List<Territory> territories;

	public Map() {
		super();
	}

	public Map(List<Continent> continents, List<Territory> territories) {
		super();
		this.continents = continents;
		this.territories = territories;
	}

	public List<Continent> getContinents() {
		return continents;
	}

	public void setContinents(List<Continent> continents) {
		this.continents = continents;
	}

	public List<Territory> getTerritories() {
		return territories;
	}

	public void setTerritories(List<Territory> territories) {
		this.territories = territories;
	}

	public String getCurrentMap() {
		return currentMap;
	}

	public void setCurrentMap(String currentMap) {
		this.currentMap = currentMap;
	}
}
