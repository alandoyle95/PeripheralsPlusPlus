package com.austinv11.peripheralsplusplus;

import com.austinv11.peripheralsplusplus.blocks.ChatBox;
import com.austinv11.peripheralsplusplus.blocks.PlayerSensor;
import com.austinv11.peripheralsplusplus.init.ModBlocks;
import com.austinv11.peripheralsplusplus.init.ModItems;
import com.austinv11.peripheralsplusplus.init.Recipes;
import com.austinv11.peripheralsplusplus.proxy.CommonProxy;
import com.austinv11.peripheralsplusplus.reference.Reference;
import com.austinv11.peripheralsplusplus.tiles.TileEntityChatBox;
import com.austinv11.peripheralsplusplus.turtles.*;
import com.austinv11.peripheralsplusplus.utils.ConfigurationHandler;
import com.austinv11.peripheralsplusplus.utils.IconManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid= Reference.MOD_ID,name = Reference.MOD_NAME,version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class PeripheralsPlusPlus {

	@Mod.Instance(Reference.MOD_ID)
	public static PeripheralsPlusPlus instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		IconManager.upgrades.add(new TurtleCompass());
		FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
		MinecraftForge.EVENT_BUS.register(new TileEntityChatBox.ChatListener());
		MinecraftForge.EVENT_BUS.register(new IconManager());
		ModItems.init();
		ModBlocks.init();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		Recipes.init();
		proxy.registerTileEntities();
		ComputerCraftAPI.registerPeripheralProvider(new ChatBox());
		ComputerCraftAPI.registerPeripheralProvider(new PlayerSensor());
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleChatBox());
		ComputerCraftAPI.registerTurtleUpgrade(new TurtlePlayerSensor());
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleCompass());
		ComputerCraftAPI.registerTurtleUpgrade(new TurtleXP());
		if (Loader.isModLoaded("factorization") || Loader.isModLoaded("JABBA"))
			ComputerCraftAPI.registerTurtleUpgrade(new TurtleBarrel());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}
