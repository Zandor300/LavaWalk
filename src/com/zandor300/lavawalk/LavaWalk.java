package com.zandor300.lavawalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.Metrics;

@SuppressWarnings("deprecation")
public class LavaWalk extends JavaPlugin implements Listener {

	public static String prefix = "§4[LavaWalk]§f ";
	public static String prefixc = "[LavaWalk] ";

	public static List<String> playerList = new ArrayList<String>();

	public final Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onEnable() {
		log.info(prefixc + "Registering events...");
		this.getServer().getPluginManager().registerEvents(this, this);
		log.info(prefixc + "Registering events done!");
		log.info(prefixc + "Sending stats to MCStats...");
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			log.info(prefixc + "Sending stats to MCStats done!");
		} catch (IOException e) {
			log.info(prefixc + "Sending stats to MCStats failed!");
		}
		log.info(prefixc + "enabled!");
	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(prefix
					+ "You need to be a player to use this command!");
		}
		Player player = (Player) sender;
		if (commandLabel.equalsIgnoreCase("lavawalk")) {
			if (args.length == 0) {
				if (!player.hasPermission("lavawalk.help") || !player.isOp()) {
					player.sendMessage(prefix
							+ "You don't have permission to use this command!");
					return false;
				}
				player.sendMessage("Name:   LavaWalk");
				player.sendMessage("Author: Zandor300");
				player.sendMessage(ChatColor.BOLD + "Help:");
				player.sendMessage("/lavawalk toggle [player] - Toggle LavaWalk for self or [player]");
			} else if (args[0].equalsIgnoreCase("toggle")) {
				if (!player.hasPermission("lavawalk.toggle") || !player.isOp()) {
					player.sendMessage(prefix
							+ "You don't have permission to use this command!");
					return false;
				}
				Player targetPlayer = player;
				if (args.length == 2) {
					targetPlayer = Bukkit.getPlayer(args[1].toString());
					if (!player.hasPermission("lavawalk.toggle.others")
							|| !player.isOp()) {
						player.sendMessage(prefix
								+ "You don't have permission to use this command!");
						return false;
					}
				}

				if (playerList.contains(targetPlayer.getName())) {
					playerList.remove(targetPlayer.getName());
					player.sendMessage(prefix + "LavaWalk disabled for "
							+ targetPlayer.getName() + ".");
				} else {
					playerList.add(targetPlayer.getName());
					player.sendMessage(prefix + "LavaWalk enabled for "
							+ targetPlayer.getName() + ".");
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("lavawalk.defaulton")) {
			playerList.add(player.getName());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		playerList.remove(player.getName());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!playerList.contains(player.getName()))
			return;
		if (!player.hasPermission("lavawalk.use") || !player.isOp())
			return;
		Location location = player.getLocation();
		location.setY(location.getY() - 2);
		final Block block = location.getWorld().getBlockAt(location);

		if (block.getType() == Material.LAVA) {
			block.setType(Material.OBSIDIAN);
			Bukkit.getScheduler().runTaskLaterAsynchronously(this,
					new BukkitRunnable() {
						@Override
						public void run() {
							block.setType(Material.LAVA);
						}
					}, 40);
		}
	}
}
