package io.github.moulberry.rpg;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import io.github.moulberry.rpg.guis.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFakePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class FakeEntityManager {

    private static final FakeEntityManager INSTANCE = new FakeEntityManager();

    public static FakeEntityManager getInstance() {
        return INSTANCE;
    }

    private static final FieldAccessor ENTITY_ID = Accessors.getFieldAccessor(
            MinecraftReflection.getEntityClass(), "entityCount", true);

    private HashMap<Integer, FakeEntity> entities = new HashMap<>();

    public class FakeEntity {
        private EntityType entityType;
        private Location serverLocation;
        private boolean allPlayersObserve;

        private List<Player> observers;
        private Location playerLocation;
        private int entityId;

        private FakeEntity(EntityType entityType, Location serverLocation, boolean allPlayersObserve) {
            this.entityType = entityType;
            this.serverLocation = serverLocation;
            this.allPlayersObserve = allPlayersObserve;

            playerLocation = serverLocation.clone();
            entityId = getNewEntityId();

            if(allPlayersObserve) {
                for(Player player : serverLocation.getWorld().getPlayers()) {
                    sendSpawnEntityToPlayer(player, entityType, entityId, serverLocation);
                }
            }
        }

        public void addObserver(Player player) {
            if(!allPlayersObserve && !observers.contains(player)) {
                sendSpawnEntityToPlayer(player, entityType, entityId, serverLocation);
                observers.add(player);
            }
        }

        public void removeObserver(Player player) {
            if(!allPlayersObserve && observers.remove(player)) {
                sendDestroyEntityToPlayer(player, entityId);
                observers.remove(player);
            }
        }
    }

    public FakeEntity createFakeEntity(EntityType entityType, Location serverLocation, boolean allPlayersObserve) {
        return new FakeEntity(entityType, serverLocation, allPlayersObserve);
    }

    private int getNewEntityId() {
        int id = (int)ENTITY_ID.get(null);
        ENTITY_ID.set(null, id + 1);
        return id;
    }

    private void sendDestroyEntityToPlayer(Player player, int id) {
        PacketContainer packet = MbRPG.getInstance().protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);

        packet.getIntegerArrays().write(0, new int[]{id});

        try {
            MbRPG.getInstance().protocolManager.sendServerPacket(player, packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSpawnEntityToPlayer(Player player, EntityType type, int id, Location location) {
        PacketContainer packet = MbRPG.getInstance().protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);

        packet.getIntegers().write(0, id);
        packet.getIntegers().write(1, (int)type.getTypeId());
        packet.getIntegers().write(2, (int)Math.floor(location.getX()*32));
        packet.getIntegers().write(3, (int)Math.floor(location.getY()*32));
        packet.getIntegers().write(4, (int)Math.floor(location.getZ()*32));

        try {
            MbRPG.getInstance().protocolManager.sendServerPacket(player, packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
