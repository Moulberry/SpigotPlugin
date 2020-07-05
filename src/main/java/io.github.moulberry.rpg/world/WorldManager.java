package io.github.moulberry.rpg.world;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.google.gson.Gson;
import io.github.moulberry.rpg.Util;
import io.github.moulberry.rpg.WorldgenConfig;
import io.github.moulberry.rpg.worldgen.ChunkGeneratorManager;
import net.minecraft.server.v1_8_R3.ChunkProviderServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class WorldManager {

    private static final WorldManager INSTANCE = new WorldManager();

    public static WorldManager getInstance() {
        return INSTANCE;
    }

    private HashMap<String, Integer> worldUnloadTimer = new HashMap<>();

    private static final FieldAccessor CHUNK_LOADER_FIELD = Accessors.getFieldAccessor(
            ChunkProviderServer.class, "chunkLoader", true);

    long currentInstanceID = 0;

    public void tickUnload() {
        List<String> toRemove = new ArrayList<>();

        for(String worldName : worldUnloadTimer.keySet()) {
            if(Bukkit.getWorld(worldName) == null) {
                //world isnt loaded
                int ticks = worldUnloadTimer.get(worldName);

                if(ticks >= 20*60) {
                    File worldFile = new File(Bukkit.getWorldContainer(), worldName);
                    Util.recursiveDelete(worldFile);

                    toRemove.add(worldName);
                } else {
                    worldUnloadTimer.put(worldName, ticks + 1);
                }
            }
        }
        for(String str : toRemove) {
            worldUnloadTimer.remove(str);
        }
        for(World world : Bukkit.getWorlds()) {
            worldUnloadTimer.put(world.getName(), 0);
            if(world.getPlayers().isEmpty()) {
                Bukkit.unloadWorld(world, world.isAutoSave());
            }
        }
    }

    public World createEmptyWorld(String name) {
        WorldCreator wc = new WorldCreator(name);

        wc.generator(new ChunkGenerator() {
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return Bukkit.createChunkData(world);
            }
        });

        return wc.createWorld();
    }

    public DynamicWorldRef getFirstDynamicWorld(String template, UUID player) {
        List<WorldManager.DynamicWorldRef> refs = WorldManager.getInstance().getDynamicWorlds(template, player);
        WorldManager.DynamicWorldRef ref;

        if(refs.isEmpty()) {
            ref = new WorldManager.DynamicWorldRef(template, player, System.currentTimeMillis());
            WorldManager.getInstance().createDynamicWorldFromTemplate(ref);
        } else {
            ref = refs.get(0);
        }

        return ref;
    }

    public List<DynamicWorldRef> getDynamicWorlds(String templateName, UUID player) {
        List<DynamicWorldRef> refs = new ArrayList<>();
        File folder = new File(Bukkit.getWorldContainer(), "worlds_dynamic/"+player.toString()+"/"+templateName);

        if(folder == null || !folder.isDirectory()) {
            return refs;
        }

        for(String ids : folder.list()) {
            refs.add(new DynamicWorldRef(templateName, player, Long.parseLong(ids)));
        }

        return refs;
    }

    public List<World> getInstanceWorlds(String templateName) {
        List<World> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            if(world.getName().startsWith("instance-"+templateName+"-")) {
                worlds.add(world);
            }
        }
        return worlds;
    }

    public World createInstanceWorldFromDynamic(DynamicWorldRef ref) {
        String instanceName = ref.toName();

        for(World world : Bukkit.getWorlds()) {
            if(world.getName().equals(instanceName)) {
                return world;
            }
        }

        WorldCreator wc = new WorldCreator(instanceName);
        wc.generator(ChunkGeneratorManager.getInstance().getEmptyGenerator());

        try {
            File worldFile = new File(Bukkit.getWorldContainer(), instanceName);
            File dynamic = new File(Bukkit.getWorldContainer(), ref.getWorldFolder());
            File worldgenConfig = new File(dynamic, "mbworldgen.json");

            if(worldgenConfig.exists()) {
                Gson gson = new Gson();
                WorldgenConfig config = gson.fromJson(new FileReader(worldgenConfig), WorldgenConfig.class);

                ChunkGenerator generator = ChunkGeneratorManager.getInstance().getGeneratorForString(config.worldGenType);
                wc.generator(generator);
            }


            if(worldFile.exists()) worldFile.delete();
            Files.createSymbolicLink(worldFile.toPath(), dynamic.toPath());
            worldFile.deleteOnExit();
        } catch(IOException e) {
            e.printStackTrace();
        }

        World instance = wc.createWorld();
        //CHUNK_LOADER_FIELD.set(((CraftWorld)instance).getHandle().chunkProviderServer, new CustomChunkLoader());
        instance.setAutoSave(true);

        return instance;
    }

    public void createDynamicWorldFromTemplate(DynamicWorldRef ref) {
        File template = new File(Bukkit.getWorldContainer(), "worlds_template/"+ref.templateName);
        if(template.exists()) Util.copyFileStructure(template, new File(Bukkit.getWorldContainer(), ref.getWorldFolder()));
    }

    public World createInstanceWorldFromTemplate(String templateName) {
        templateName = Util.cleanFilename(templateName);
        String instanceName = "instance-"+templateName+"-"+(++currentInstanceID);

        WorldCreator wc = new WorldCreator(instanceName);
        wc.generator(new ChunkGenerator() {
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return Bukkit.createChunkData(world);
            }
        });
        File worldFile = new File(Bukkit.getWorldContainer(), instanceName);
        Util.copyFileStructure(new File(Bukkit.getWorldContainer(), "worlds_template/"+templateName),
                worldFile);
        Util.recursiveDeleteOnExit(worldFile);

        World instance = wc.createWorld();
        instance.setAutoSave(false);

        return instance;
    }

    public static DynamicWorldRef dynamicWorldRefFromName(String name) {
        String[] split = name.split("-");

        if(split.length == 4) {
            if(split[0].equals("dyninstance")) {
                String templateName = split[1];
                UUID player = Util.UUIDfromString(split[2]);
                long creationTime = Long.parseLong(split[3]);

                return new DynamicWorldRef(templateName, player, creationTime);
            }
        }

        return null;
    }

    public static class DynamicWorldRef {
        public String templateName;
        public UUID player;
        public long creationTime;

        public DynamicWorldRef(String templateName, UUID player, long creationTime) {
            templateName = Util.cleanFilename(templateName);
            this.templateName = templateName;
            this.player = player;
            this.creationTime = creationTime;
        }

        public String toString() {
            templateName = Util.cleanFilename(templateName);
            return "dynamic_world_ref{"+templateName+","+
                    player.toString().replaceAll("-","")+","+creationTime+"}";
        }

        public String toName() {
            templateName = Util.cleanFilename(templateName);
            return "dyninstance-"+templateName+"-"+
                    player.toString().replaceAll("-","")+"-"+creationTime;
        }



        public String getWorldFolder() {
            templateName = Util.cleanFilename(templateName);
            return "worlds_dynamic/"+player.toString()+"/"+templateName+"/"+creationTime;
        }
    }



}
