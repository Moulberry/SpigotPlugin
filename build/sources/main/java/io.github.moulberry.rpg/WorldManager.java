package io.github.moulberry.rpg;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.io.*;
import java.util.*;

public class WorldManager {

    private static final WorldManager INSTANCE = new WorldManager();

    public static WorldManager getInstance() {
        return INSTANCE;
    }

    long currentInstanceID = 0;

    public World createEmptyWorld(String name) {
        WorldCreator wc = new WorldCreator(name);

        wc.generator(new ChunkGenerator() {
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return Bukkit.createChunkData(world);
            }
        });

        return wc.createWorld();
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

    public World createInstanceWorldFromDynamic(DynamicWorldRef ref) {
        String instanceName = ref.toName();

        WorldCreator wc = new WorldCreator(instanceName);
        wc.generator(new ChunkGenerator() {
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return Bukkit.createChunkData(world);
            }
        });
        Util.copyFileStructure(new File(Bukkit.getWorldContainer(), ref.getWorldFolder()),
                new File(Bukkit.getWorldContainer(), instanceName));

        World instance = wc.createWorld();
        instance.setAutoSave(true);

        return instance;
    }

    public void copyDynamicWorld(DynamicWorldRef ref) {
        Util.copyFileStructure(new File(Bukkit.getWorldContainer(), "worlds_template/"+ref.templateName),
                new File(Bukkit.getWorldContainer(), ref.getWorldFolder()));
    }

    public World createInstanceWorldFromTemplate(String templateName) {
        String instanceName = "instance-"+templateName+"-"+(++currentInstanceID);

        WorldCreator wc = new WorldCreator(instanceName);
        wc.generator(new ChunkGenerator() {
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return Bukkit.createChunkData(world);
            }
        });
        Util.copyFileStructure(new File(Bukkit.getWorldContainer(), "worlds_template/"+templateName),
                new File(Bukkit.getWorldContainer(), instanceName));

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
            this.templateName = templateName;
            this.player = player;
            this.creationTime = creationTime;
        }

        public String toString() {
            return "dynamic_world_ref{"+templateName+","+
                    player.toString().replaceAll("-","")+","+creationTime+"}";
        }

        public String toName() {
            return "dyninstance-"+templateName+"-"+
                    player.toString().replaceAll("-","")+"-"+creationTime;
        }



        public String getWorldFolder() {
            return "worlds_dynamic/"+player.toString()+"/"+templateName+"/"+creationTime;
        }
    }



}
