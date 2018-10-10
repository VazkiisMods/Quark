package vazkii.quark.world.feature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * this is backwards and modern dungeon tweaks compatible
 * 
 * @author jredfox
 *
 */
public class DungeonTweaksCompat
{

    public static boolean isLegacy = false;
    public static boolean isLoaded = false;

    /**
     * make backwards compatability when isLegacy becomes true
     */
    public static void legacyCheck()
    {
        isLoaded = Loader.isModLoaded("dungeontweaks");
        
        if(!isLoaded)
        {
            return;
        }
        
        try
        {
            Class c = Class.forName("com.EvilNotch.dungeontweeks.main.Events.EventDungeon$Post");
            isLegacy = true;
        }
        catch (Throwable t)
        {
        	
        }
    }

    /**
     * register all dungeon tweaks mobs to anydim towers
     */
    public static void registerDungeons()
    {
        if (!isLoaded || isLegacy)
        {
            return;//do not support legacy versions lists as they were a complete mess let people using legacy either update or config themselves
        }

        try
        {
        	Class dungeonMobs = Class.forName("com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs");
        	
            Method cacheForge = dungeonMobs.getMethod("cacheForge");
            cacheForge.invoke(null);
            
            Method addDungeonMob = dungeonMobs.getMethod("addDungeonMob", ResourceLocation.class, DungeonMob.class);
            Method getDungeonList = dungeonMobs.getMethod("getDungeonList", ResourceLocation.class,boolean.class);
            List<DungeonMob> list = (List<DungeonMob>)getDungeonList.invoke(null, new ResourceLocation("dungeon"), true);
            
            for(DungeonMob mob : list)
            {
            	addDungeonMob.invoke(null, quark, mob);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * fire the event based upon tower definitions
     */
	public static final ResourceLocation quark = new ResourceLocation("quark:dungeon");
    public static void fireDungeonSpawn(TileEntity tile, World world, Random random)
    {
        if (isLegacy)
        {
            try
            {
                @SuppressWarnings("unchecked")
                Constructor<? extends Event> constructor = (Constructor<? extends Event>) Class.forName("com.EvilNotch.dungeontweeks.main.Events.EventDungeon$Post").getConstructor(TileEntity.class,
                        BlockPos.class, Random.class, ResourceLocation.class, World.class);
                Event event = constructor.newInstance(tile, tile.getPos(), random, quark, world);
                MinecraftForge.EVENT_BUS.post(event);
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
        else
        {
            try
            {
                Method fireDungeonTweaks = Class.forName("com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs").getMethod("fireDungeonTweaks", ResourceLocation.class, TileEntity.class,
                        Random.class, World.class);
                fireDungeonTweaks.invoke(null, quark, tile, random, world);
                System.out.println("Dungeon At:" + tile.getPos());
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
    }

}
