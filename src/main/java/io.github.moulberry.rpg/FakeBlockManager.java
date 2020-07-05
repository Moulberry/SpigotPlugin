package io.github.moulberry.rpg;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class FakeBlockManager {

    private static final FakeBlockManager INSTANCE = new FakeBlockManager();

    public static FakeBlockManager getInstance() {
        return INSTANCE;
    }

    private HashMap<Player, Map<Location, FakeBlockData>> fakeBlockMap = new HashMap<>();

    public class FakeBlockData {
        public Material mat;
        public byte data;
        public final long created;
        public long maxMillis = -1;

        public FakeBlockData(Material mat, byte data) {
            this.mat = mat;
            this.data = data;

            this.created = System.currentTimeMillis();
        }
    }

    private Map<Location, FakeBlockData> getMapForPlayerMod(Player player) {
        Map<Location, FakeBlockData> map = fakeBlockMap.get(player);

        if(map == null) {
            map = new HashMap<>();
            fakeBlockMap.put(player, map);
        }

        return map;
    }

    public void onTick() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            Map<Location, FakeBlockData> map = fakeBlockMap.get(player);

            if(map == null) continue;

            //Remove fake blocks which have timed out
            List<Location> toRemove = new ArrayList<>();
            for(Location loc : map.keySet()) {
                if(map.get(loc).maxMillis < 0) continue;
                if(map.get(loc).created + map.get(loc).maxMillis <= System.currentTimeMillis()) {
                    toRemove.add(loc);
                }
            }
            for(Location loc : toRemove) {
                removeFakeBlock(player, loc);
            }
        }
    }

    public Map<Location, FakeBlockData> getMapForPlayer(Player player) {
        return Collections.unmodifiableMap(getMapForPlayerMod(player));
    }

    public void putFakeBlock(Player player, Location location, Material material, byte data) {
        FakeBlockData old = getMapForPlayerMod(player).get(location);

        getMapForPlayerMod(player).put(location, new FakeBlockData(material, data));

        if(old != null) {
            blockChange(player, location, old.mat, old.data, material, data);
        } else {
            player.sendBlockChange(location, material, data);
        }
    }

    public void putFakeBlock(Player player, Location location, Material material, byte data, long maxMillis) {
        FakeBlockData old = getMapForPlayerMod(player).get(location);

        FakeBlockData fakeBlockData = new FakeBlockData(material, data);
        fakeBlockData.maxMillis = maxMillis;
        getMapForPlayerMod(player).put(location, fakeBlockData);

        if(old != null) {
            blockChange(player, location, old.mat, old.data, material, data);
        } else {
            player.sendBlockChange(location, material, data);
        }


    }

    public void clearAllUnsafe(Player player) {
        fakeBlockMap.remove(player);
    }

    public void removeFakeBlock(Player player, Location location) {
        Map<Location, FakeBlockData> map = fakeBlockMap.get(player);
        Material matOld = map.get(location).mat;
        byte dataOld = map.get(location).data;
        map.remove(location);

        Block b = player.getWorld().getBlockAt(location);
        blockChange(player, location, matOld, dataOld, b.getType(), b.getData());
    }

    private void blockChange(Player player, Location location, Material matOld, byte dataOld, Material matNew, byte dataNew) {
        if(matOld != matNew || dataOld != dataNew) {
            player.sendBlockChange(location, matNew, dataNew);
        }
    }

}
