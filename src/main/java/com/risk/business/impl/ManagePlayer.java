package com.risk.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.risk.business.IManagePlayer;
import com.risk.file.impl.ManageGamePlayFile;
import com.risk.model.Card;
import com.risk.model.Continent;
import com.risk.model.GamePlay;
import com.risk.model.GamePlayTerritory;
import com.risk.model.Map;
import com.risk.model.Player;
import com.risk.model.Territory;

/**
 * This class is responsible for setting up Startup Phase of the Game
 * ,converting list of current player object to game play file object.
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @version 0.0.1
 */
@Service
public class ManagePlayer implements IManagePlayer {

	private List<Player> player_info_list;

	/**
	 * @see com.risk.business.IManagePlayer#createPlayer(int, java.lang.String,
	 *      java.lang.String)
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @author <a href="mayankjariwala1994@gmail.com"> Mayank Jariwala </a>
	 *         Modifications of function performed by Mayank Jariwala
	 */
	@Override
	public GamePlay createPlayer(int num_of_players, String file_name, String army_allocation_type) {
		GamePlay game_state = null;
		player_info_list = new ArrayList<Player>();
		int army_stock = getArmyStock(num_of_players);
		for (int i = 1; i <= num_of_players; i++) {
			String player_name = "player" + i;
			List<GamePlayTerritory> gameplay_territory_list = new ArrayList<>();
			List<Card> card_list = new ArrayList<Card>();
			Player p = new Player();
			p.setId(i);
			p.setName(player_name);
			p.setArmy_stock(army_stock);
			p.setTerritory_list(gameplay_territory_list);
			p.setCard_list(card_list);
			player_info_list.add(p);
		}
		ManageMap manage_map_object = new ManageMap();
		ManageGamePlay game_manager = new ManageGamePlay();
		Map map = new Map();
		map = manage_map_object.getFullMap(file_name);
		if (map != null && map.getStatus().equalsIgnoreCase("")) {
			assingTerritoriesToPlayers(map);
			if (army_allocation_type.equalsIgnoreCase("A")) {
				assignArmiesOnTerritories(army_stock);
			}
			game_state = writePlayerToFile(player_info_list, file_name);
		} else if (map != null && map.getStatus() != "") {
			game_state = new GamePlay();
			game_state.setStatus(map.getStatus());
		} else {
			game_state = new GamePlay();
			game_state.setStatus("Invalid Map");
		}
		game_manager.calculateArmiesReinforce(game_state.getGame_state(), map);
		return game_state;
	}

	/**
	 * This method is used for assigning armies on territories
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * 
	 * @param army_stock number of armies assigned to territories
	 */
	private void assignArmiesOnTerritories(int army_stock) {
		for (int player_ingo_list_size = 0; player_ingo_list_size < player_info_list.size(); player_ingo_list_size++) {
			player_info_list.get(player_ingo_list_size).setArmy_stock(0);
			int i = 0;
			for (int territory_list_size = 0; territory_list_size < player_info_list.get(player_ingo_list_size)
					.getTerritory_list().size(); territory_list_size++) {
				if (i < army_stock) {
					int sum_armies = player_info_list.get(player_ingo_list_size).getTerritory_list()
							.get(territory_list_size).getNumber_of_armies() + 1;
					player_info_list.get(player_ingo_list_size).getTerritory_list().get(territory_list_size)
							.setNumber_of_armies(sum_armies);
					if (territory_list_size + 1 == player_info_list.get(player_ingo_list_size).getTerritory_list()
							.size()) {
						territory_list_size = -1;
					}
					i++;
				} else {
					break;
				}
			}
		}
	}

	/**
	 * This method is an abstraction for the process of converting player Object
	 * into the Game Play File to be saved/loaded.
	 * 
	 * @author <a href="mayankjariwala1994@gmail.com"> Mayank Jariwala </a>
	 *         Modifications done by Mayank Jariwala
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param player_info_list This is the entire Player Object which will be
	 *                         converted to a Game Play File Object and then written
	 *                         on to a GamePlay File.
	 * @param file_name        Name of the Map file to be stored in Resource Folder
	 *                         and GamePlay File.
	 * @return GamePlay File Object
	 */
	private GamePlay writePlayerToFile(List<Player> player_info_list, String file_name) {
		List<Player> player_list_at_file = convertPlayerToFileLayer(player_info_list);
		ManageGamePlayFile manage_game_play_file = new ManageGamePlayFile();
		String game_phase = "Startup";
		file_name = (file_name.endsWith(".map") ? file_name.split("\\.")[0] : file_name) + "_"
				+ String.valueOf(System.currentTimeMillis());
		GamePlay game_state = new GamePlay();
		game_state.setFile_name(file_name);
		game_state.setGame_phase(game_phase);
		game_state.setGame_state(player_list_at_file);
		boolean file_write_message = manage_game_play_file.saveGameStateToDisk(game_state);
		if (file_write_message)
			return game_state;
		else
			return null;
	}

	/**
	 * This function just convert current Player Object to Game Play File Layer
	 * Object
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param player_list Entire Player List which will needed to be converted to
	 *                    Game play file object
	 * @return list of converted Game play file object
	 */
	private List<Player> convertPlayerToFileLayer(List<Player> player_list) {
		List<Player> player_list_at_file = new ArrayList<>();
		for (int i = 0; i < player_list.size(); i++) {

			Player player_object_at_file = new Player();
			List<GamePlayTerritory> game_play_territory_list = new ArrayList<>();
			List<Card> card_list = new ArrayList<>();
			player_object_at_file.setName(player_list.get(i).getName());
			player_object_at_file.setId(player_list.get(i).getId());
			player_object_at_file.setArmy_stock(player_list.get(i).getArmy_stock());

			for (int j = 0; j < player_list.get(i).getTerritory_list().size(); j++) {
				GamePlayTerritory game_play_territory = new GamePlayTerritory();
				game_play_territory
						.setTerritory_name(player_list.get(i).getTerritory_list().get(j).getTerritory_name());
				game_play_territory
						.setContinent_name(player_list.get(i).getTerritory_list().get(j).getContinent_name());
				game_play_territory
						.setNumber_of_armies(player_list.get(i).getTerritory_list().get(j).getNumber_of_armies());
				game_play_territory_list.add(game_play_territory);
			}
			player_object_at_file.setTerritory_list(game_play_territory_list);

			for (int k = 0; k < player_list.get(i).getCard_list().size(); k++) {
				Card card = new Card();
				card.setTerritory_name(player_list.get(i).getCard_list().get(k).getTerritory_name());
				card.setArmy_type(player_list.get(i).getCard_list().get(k).getArmy_type());

				card_list.add(card);
			}
			player_object_at_file.setCard_list(card_list);

			player_list_at_file.add(player_object_at_file);
		}
		return player_list_at_file;
	}

	/**
	 * This method is responsible for an initial distribution of armies in Startup
	 * Phase of the game.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param num_of_players Number of players playing the game
	 * @return Army count being received by each Player.
	 */
	private int getArmyStock(int num_of_players) {
		int army_stock = 0;
		if (num_of_players == 2) {
			army_stock = 40;
		} else if (num_of_players == 3) {
			army_stock = 35;
		} else if (num_of_players == 4) {
			army_stock = 30;
		} else if (num_of_players == 5) {
			army_stock = 25;
		} else if (num_of_players == 6) {
			army_stock = 20;
		}
		return army_stock;
	}

	/**
	 * This method is an abstraction for the process of initially allocating
	 * territories to each player, in a round robin fashion.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param map Map Object for the active map in Game.
	 */
	private void assingTerritoriesToPlayers(Map map) {
		List<GamePlayTerritory> territories = getTerritories(map);
		int total_player = player_info_list.size();
		int count = -1;
		for (int i = 0; i < territories.size(); i++) {
			count++;
			if (player_info_list.get(count) != null) {
				try {
					player_info_list.get(count).getTerritory_list().add(territories.get(i));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			} else {
				continue;
			}
			if (count == total_player - 1) {
				count = -1;
			}
		}
	}

	/**
	 * This method is randomly linking territories to game play territory object
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param map Map Object for retrieving Territories.
	 * @return List of territories
	 */
	private List<GamePlayTerritory> getTerritories(Map map) {
		Continent map_continent;
		Territory map_territory;
		HashMap<String, Continent> continents = new HashMap<String, Continent>();
		continents = map.getContinents();
		List<Territory> temp_territory_list;
		String continent_name;
		ArrayList<GamePlayTerritory> total_territory_list = new ArrayList<GamePlayTerritory>();

		for (Entry<String, Continent> m : continents.entrySet()) {
			continent_name = m.getKey();
			map_continent = m.getValue();
			temp_territory_list = map_continent.getTerritories();
			for (int i = 0; i < temp_territory_list.size(); i++) {
				map_territory = temp_territory_list.get(i);
				GamePlayTerritory game_play_territory = new GamePlayTerritory();
				game_play_territory.setTerritory_name(map_territory.getName());
				game_play_territory.setContinent_name(continent_name);
				game_play_territory.setNumber_of_armies(0);
				total_territory_list.add(game_play_territory);
			}
		}
		return total_territory_list;
	}
}