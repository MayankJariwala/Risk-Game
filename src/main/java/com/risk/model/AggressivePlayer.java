package com.risk.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.risk.business.AbstractPlayer;
import com.risk.business.impl.ManageGamePlay;
import com.risk.business.impl.ManagePlayer;

/**
 * This Player Model represents a Aggressive Computer Player in terms of
 * Strategy-Pattern implementation, during our GamePlay.
 * 
 * @author <a href="mailto:apoorv.semwal20@gmail.com">Apoorv Semwal</a>
 * @version 0.0.1
 */
public class AggressivePlayer extends AbstractPlayer {

	/**
	 * Reinforcement of an Aggressive Player.
	 * 
	 * @see com.risk.business.IAbstractPLayer#reinforce(GamePlay)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	public GamePlay reinforce(GamePlay game_play) {

		ManagePlayer player_manager = new ManagePlayer();
		ManageGamePlay game_manager = new ManageGamePlay();
		AbstractPlayer current_player = null;
		GamePlayTerritory strongest_territory = null;

		if (game_play != null) {

			game_manager.calculateArmiesReinforce(game_play.getGame_state(), game_play.getMap(),
					game_play.getCurrent_player());

			for (AbstractPlayer player : game_play.getGame_state()) {
				if (player.getId() == game_play.getCurrent_player()) {
					current_player = player;
					break;
				} else {
					continue;
				}
			}

			if (current_player != null) {
				if (current_player.getCard_list().size() > 4) {
					player_manager.tradeCards(game_play);
				}
				int max = 0;
				for (GamePlayTerritory territory : current_player.getTerritory_list()) {
					if (territory.getNumber_of_armies() > max) {
						max = territory.getNumber_of_armies();
						strongest_territory = territory;
					}
				}
			}

			if (strongest_territory != null) {
				strongest_territory.setNumber_of_armies(
						strongest_territory.getNumber_of_armies() + current_player.getArmy_stock());
				current_player.setArmy_stock(0);
			}
		}
		return game_play;
	}

	/**
	 * Attack of an Aggressive Player.
	 * 
	 * @see com.risk.business.IAbstractPLayer#attack(GamePlay)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	public GamePlay attack(GamePlay game_play) {

		AbstractPlayer current_player = null;
		AbstractPlayer defender_player = null;
		Attack attack_details = null;
		GamePlayTerritory strongest_territory = null;
		List<String> neighbours = new ArrayList<>();
		List<String> player_territories = new ArrayList<>();
		ManagePlayer player_manager = new ManagePlayer();
		GamePlayTerritory defender_territory_data = null;

		for (AbstractPlayer player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				current_player = player;
				break;
			} else {
				continue;
			}
		}

		if (current_player != null) {

			int max = 0;
			for (GamePlayTerritory territory : current_player.getTerritory_list()) {
				player_territories.add(territory.getTerritory_name());
				if (territory.getNumber_of_armies() > max) {
					max = territory.getNumber_of_armies();
					strongest_territory = territory;
				}
			}

			for (com.risk.model.gui.Territory territory : game_play.getGui_map().getTerritories()) {
				if (territory.getName().equalsIgnoreCase(strongest_territory.getTerritory_name())) {
					neighbours = Arrays.asList(territory.getNeighbours().split(";"));
					break;
				} else {
					continue;
				}
			}

			if (strongest_territory != null) {

				for (com.risk.model.gui.Territory defender_territory : game_play.getGui_map().getTerritories()) {
					if (strongest_territory.getNumber_of_armies() <= 1) {
						break;
					}
					if (defender_territory.getName().equalsIgnoreCase(strongest_territory.getTerritory_name())
							|| player_territories.contains(defender_territory.getName())
							|| !neighbours.contains(defender_territory.getName())) {
						continue;
					} else {
						for (AbstractPlayer defender : game_play.getGame_state()) {
							defender_player = null;
							defender_territory_data = null;
							if (defender.getId() == current_player.getId()) {
								continue;
							}
							for (GamePlayTerritory defend_territory : defender.getTerritory_list()) {
								if (defend_territory.getTerritory_name()
										.equalsIgnoreCase(defender_territory.getName())) {
									defender_player = defender;
									defender_territory_data = defend_territory;
									break;
								}
							}
							if (defender_player != null) {
								break;
							}
						}

						if (defender_player != null) {
							attack_details = new Attack();
							attack_details.setAttacker_territory(strongest_territory.getTerritory_name());
							attack_details.setDefender_territory(defender_territory.getName());
							game_play.setGame_phase("ATTACK_ALL_OUT");
							game_play.setAttack(attack_details);
							player_manager.attack(game_play);
							if (game_play.getStatus().contains("Attacker Occupies Defender Territory")) {
								strongest_territory.setNumber_of_armies(strongest_territory.getNumber_of_armies() - 1);
								defender_territory_data.setNumber_of_armies(1);
								player_territories.add(defender_territory_data.getTerritory_name());
								current_player.getTerritory_list().set(
										current_player.getTerritory_list().indexOf(strongest_territory),
										strongest_territory);
								current_player.getTerritory_list().set(
										current_player.getTerritory_list().indexOf(defender_territory_data),
										defender_territory_data);
							} else {
								break;
							}
						}
					}
				}
			}
		}
		return game_play;
	}

	/**
	 * Fortify of an Aggressive Player.
	 * 
	 * @see com.risk.business.IAbstractPLayer#fortify(GamePlay)
	 * @author <a href="mailto:a_semwal@encs.concordia.ca">ApoorvSemwal</a>
	 */
	public GamePlay fortify(GamePlay game_play) {

		AbstractPlayer current_player = null;
		Boolean neighbour_flag = false;

		for (AbstractPlayer player : game_play.getGame_state()) {
			if (player.getId() == game_play.getCurrent_player()) {
				current_player = player;
				break;
			} else {
				continue;
			}
		}

		if (current_player != null) {

			for (GamePlayTerritory territory_a : current_player.getTerritory_list()) {

				if (territory_a.getNumber_of_armies() == 1) {
					continue;
				}
				for (GamePlayTerritory territory_b : current_player.getTerritory_list()) {

					neighbour_flag = false;

					if (territory_a.getTerritory_name().equalsIgnoreCase(territory_b.getTerritory_name())
							|| territory_b.getNumber_of_armies() == 1) {
						continue;
					} else {
						neighbour_flag = checkIfNeighbours(territory_a.getTerritory_name(),
								territory_b.getTerritory_name(), game_play.getGui_map());
					}

					if (neighbour_flag == true) {
						territory_a.setNumber_of_armies(
								territory_a.getNumber_of_armies() + territory_b.getNumber_of_armies() - 1);
						territory_b.setNumber_of_armies(1);
					}
				}
			}
		}
		return game_play;
	}

	private boolean checkIfNeighbours(String territory_a, String territory_b, com.risk.model.gui.Map map) {
		Boolean neighbour_flag = false;
		for (com.risk.model.gui.Territory territory : map.getTerritories()) {
			if (!territory.getName().equalsIgnoreCase(territory_a)) {
				continue;
			} else {
				if (Arrays.asList(territory.getNeighbours().split(";")).contains(territory_b)) {
					neighbour_flag = true;
					break;
				} else {
					neighbour_flag = false;
					break;
				}
			}
		}
		return neighbour_flag;
	}
}
