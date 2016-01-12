package com.jack.treespirit.scoreboard;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerScoreboard {
    
    private static HashMap<Player, PlayerScoreboard> players = new HashMap<>();
    
    private Player     player;
    private Scoreboard scoreboard;
    private Objective  objective;
    private String     displayname;
    
    public PlayerScoreboard(Player player) {
        this.player = player;
        this.displayname = "Scoreboard";
        
        this.resetScoreboard();
    }
    
    public static void addPlayer(Player player) {
        players.put(player, new PlayerScoreboard(player));
    }
    public static void removePlayer(Player player) {
        if (players.containsKey(player))
            players.remove(player);
    }
    public static PlayerScoreboard getPlayerScoreboard(Player player) {
        if (players.containsKey(player)) return players.get(player);
        return null;
    }

    
    public void setScore(String name, int points) {
        Score score = objective.getScore(Bukkit.getOfflinePlayer(name));
        score.setScore(points);
    }
    
    public int getScore(String name) {
        return objective.getScore(Bukkit.getOfflinePlayer(name)).getScore();
    }
    
    public void sendScoreboard() {
        player.setScoreboard(scoreboard);
    }
    public void setDisplayName(String displayname) {
        if (displayname.length() > 32) displayname = displayname.substring(0, 32);
        this.displayname = displayname;
        objective.setDisplayName(this.displayname);
    }
    public void resetScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        
        objective = scoreboard.registerNewObjective(this.displayname, "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(this.displayname);
    }
}