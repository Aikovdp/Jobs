package com.gamingmesh.jobs.container;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gamingmesh.jobs.Jobs;

public class ExploreChunk {

//    private int x;
//    private int z;
    private Set<Integer> playerIds = new HashSet<>();
    private int dbId = -1;
    private boolean updated = false;

    public ExploreChunk() {
    }

//    public ExploreChunk(int x, int z) {
//	this.x = x;
//	this.z = z;
//    }

    public ExploreRespond addPlayer(int playerId) {
	if (isFullyExplored()) {
	    return new ExploreRespond(Jobs.getExplore().getPlayerAmount() + 1, false);
	}
	boolean newChunkForPlayer = false;
	if (!playerIds.contains(playerId)) {
	    if (playerIds.size() < Jobs.getExplore().getPlayerAmount()) {
		playerIds.add(playerId);
		updated = true;
	    }
	    newChunkForPlayer = true;
	}

	if (playerIds.size() >= Jobs.getExplore().getPlayerAmount()) {
	    if (Jobs.getGCManager().ExploreCompact)
		playerIds = null;
	}

	return new ExploreRespond(newChunkForPlayer ? getPlayers().size() : getPlayers().size() + 1, newChunkForPlayer);
    }

    public boolean isAlreadyVisited(int playerId) {
	return isFullyExplored() || playerIds.contains(playerId);
    }

    public int getCount() {
	return isFullyExplored() ? Jobs.getExplore().getPlayerAmount() : playerIds.size();
    }

//    public int getX() {
//	return x;
//    }
//
//    public int getZ() {
//	return z;
//    }

    public Set<Integer> getPlayers() {
	return playerIds == null ? new HashSet<>() : playerIds;
    }

    public String serializeNames() {
	if (playerIds == null)
	    return null;

	String s = "";
	for (Integer one : playerIds) {
	    if (!s.isEmpty())
		s += ";";
	    s += one;
	}
	return s;
    }

    public void deserializeNames(String names) {
	if (names == null || names.isEmpty()) {
	    playerIds = null;
	    return;
	}

	if (playerIds == null) {
	    playerIds = new HashSet<>();
	}

	List<String> split = Arrays.asList(names.split(";"));
	for (String one : split) {
	    try {
		int id = Integer.parseInt(one);
		PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(id);
		if (info != null)
		    playerIds.add(id);
	    } catch (Throwable e) {
		updated = true;
		JobsPlayer jp = Jobs.getPlayerManager().getJobsPlayer(one);
		if (jp != null)
		    playerIds.add(jp.getUserId());
	    }
	}

	if (playerIds.size() >= Jobs.getExplore().getPlayerAmount() && Jobs.getGCManager().ExploreCompact) {
	    playerIds = null;
	    if (!names.isEmpty())
		updated = true;
	}
    }

    public int getDbId() {
	return dbId;
    }

    public void setDbId(int dbId) {
	this.dbId = dbId;
    }

    public boolean isUpdated() {
	return updated;
    }

    public void setUpdated(boolean updated) {
	this.updated = updated;
    }

    public boolean isFullyExplored() {
	return playerIds == null || playerIds.size() >= Jobs.getExplore().getPlayerAmount();
    }
}
