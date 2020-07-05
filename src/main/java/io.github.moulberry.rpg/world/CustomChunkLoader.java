package io.github.moulberry.rpg.world;

import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R3.IChunkLoader;
import net.minecraft.server.v1_8_R3.World;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CustomChunkLoader implements IChunkLoader {

    private File file;

    public CustomChunkLoader(File file) {
        this.file = file;
    }

    private File getWorldFile() {
        return new File(file, "Level.mbw");
    }

    //Load chunk
    public Chunk a(World world, int x, int z) throws IOException {
        return null;
    }

    //Save chunk
    public void a(World world, Chunk chunk) throws IOException, ExceptionWorldConflict {
        /**
         * Required data:
         *      xPos
         *      zPos
         *      HeightMap (16x16 ints)
         *      Biomes (16x16 bytes)
         *      Sections (up to 16):
         *          Height
         *          BlockLight (2048 bytes)
         *          Blocks (4096 bytes)
         *          Data (2048 bytes)
         *          SkyLight (2048 bytes)
         *      Entities:
         *          NBT (Use default)
         *      TileEntities:
         *          NBT (Use default)
         */
        /**
         * Structure:
         * 2 bytes - magic (0xA1C3)
         * 1 byte - version
         * 1 byte - low chunk x
         * 1 byte - low chunk z
         * 1 byte - high chunk x
         * 1 byte - high chunk z
         * ---- 1 byte (bool) - using palette (palette only used if # unique blocks in world <256?)
         * ---- 384 bytes block data (8+4 bits per block)
         * 4 bytes - chunk 1 size (compressed) (0 if all air)
         * -> chunk 1 data (compressed)
         * 4 bytes - chunk 2 size, etc.
         * -> chunk 2 data
         * -> chunk 3 data
         */
        /**
         * Chunk data:
         * 1 byte - chunk header (0xBA11)
         * 1 byte - xPos (sanity)
         * 1 byte - zPos (sanity)
         * 1024 bytes (256 ints) - heightmap
         * 256 bytes - biomes
         * 4 bytes - entity size
         * (?) - entity data (NBT)
         * 4 bytes - tileentity size
         * (?) - tileentity data (NBT)
         * 4 bytes - section 1 size (0 if all air)
         * 4096 bytes - 16x16x16 list of block ids (if palette is set, refers to palette instead) (diffed?)
         * 2048 bytes - block data (only if palette is not set) (diffed?)
         * section 2, etc.
         *
         */
        FileOutputStream stream = new FileOutputStream(getWorldFile());
        FileChannel channel = stream.getChannel();

        ByteBuffer header = ByteBuffer.allocate(7);
        int headerRead = channel.read(header);
        if(headerRead != header.capacity()) {
            //Invalid header
            throw new IOException("Invalid header: wrong length.");
        }

        if(!(header.get(0) == 0xA1 && header.get(1) == 0xC3)) {
            //Magic not set, invalid header
            throw new IOException("Invalid header: wrong magic number.");
        }

        byte version = header.get(2);
        byte lowChunkX = header.get(3);
        byte lowChunkZ = header.get(4);
        byte highChunkX = header.get(5);
        byte highChunkZ = header.get(6);

        if(lowChunkX > highChunkX) {
            //Error, invalid header
            throw new IOException("Invalid header: lowChunkX > highChunkX.");
        }

        if(lowChunkZ > highChunkZ) {
            //Error, invalid header
            throw new IOException("Invalid header: lowChunkZ > highChunkZ.");
        }

        if(chunk.locX < lowChunkX) {
            throw new IOException("Invalid header: chunk outside of world bounds.");
        }

        if(chunk.locX > highChunkX) {
            throw new IOException("Invalid header: chunk outside of world bounds.");
        }

        if(chunk.locZ < lowChunkZ) {
            throw new IOException("Invalid header: chunk outside of world bounds.");
        }

        if(chunk.locZ > highChunkZ) {
            throw new IOException("Invalid header: chunk outside of world bounds.");
        }

        int chunkXOff = chunk.locX - lowChunkX;
        int chunkZOff = chunk.locZ - lowChunkZ;
        int zSize = highChunkZ - lowChunkZ + 1;

        int chunkID = zSize * chunkXOff + chunkZOff;

        //Skip ahead to the chunkID we want
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        for(int i=0; i<chunkID; i++) {
            sizeBuffer.rewind();
            channel.read(sizeBuffer);
            channel.position(channel.position()+sizeBuffer.get());
        }



        //stream.
    }

    //Save extra chunk data
    public void b(World world, Chunk chunk) throws IOException {

    }

    //Chunk tick
    public void a() {

    }

    //Save extra data
    public void b() {

    }
}
