package com.risk.file;

import java.util.List;

import com.risk.model.file.PlayerFile;

/**
 * Manage GamePlay Interface - It is use to save current game playing status to
 * file.
 * 
 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
 * @author <a href="mailto:mayankjariwala1994@gmail.com">Mayank Jariwala</a> -
 *         Added Interface comments
 * @version 0.0.1
 */
public interface IManageGamePlayFile {

	/**
	 * This function is use to write player data to new or existing Game Play file.
	 * 
	 * @author <a href="mailto:himansipatel1994@gmail.com">Himansi Patel</a>
	 * @param file_player_list : List of playing game player object
	 * @param file_name        : The name of map file for saving purpose
	 * @return File write status message
	 */
	Boolean savePlayerInfoToDisk(List<PlayerFile> file_player_list, String file_name);

}
