package com.mcf.davidee.autojoin;

import java.net.InetSocketAddress;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.mcf.davidee.autojoin.gui.DisconnectedScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;


@Mod(modid="autojoin", name="Auto Join", version=AutoJoin.VERSION, dependencies="after:guilib")
public class AutoJoin {
	
	public static final int PROTOCOL_VER = 340;
	public static final String VERSION = "1.11.0.0";
	
	@Instance("autojoin")
	public static AutoJoin instance;
	
	private AJConfig config;
	public ServerInfo lastServer;
	
	private GuiScreen guiCache;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new AJConfig(new Configuration(event.getSuggestedConfigurationFile()));
		
		ModMetadata modMeta = event.getModMetadata();
		modMeta.authorList = Arrays.asList(new String[] { "Davidee" });
		modMeta.autogenerated = false;
		modMeta.credits = "Thanks to Mojang, Forge, and all your support.";
		modMeta.description = "Easily join a populated public server.";
		modMeta.url = "http://www.minecraftforum.net/topic/1922957-/";
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.currentScreen != null && !mc.currentScreen.equals(guiCache)) {
			guiCache = mc.currentScreen;
			
			if (guiCache instanceof GuiDisconnected && lastServer != null) 
				mc.displayGuiScreen(new DisconnectedScreen(lastServer, (GuiDisconnected)guiCache));
			if (guiCache instanceof GuiConnecting && mc.getCurrentServerData() != null) /*getServerData*/
				lastServer = ServerInfo.from(mc.getCurrentServerData());
			if (guiCache instanceof GuiMainMenu)
				resetCache();
		}
	}
	
	@SubscribeEvent
	public void connectedToServer(ClientConnectedToServerEvent event) {
		if (event.isLocal())
			resetCache();
		else
			lastServer = ServerInfo.from((InetSocketAddress) event.getManager().getRemoteAddress());
	}
	
	public AJConfig getConfig() {
		return config;
	}
	
	public void resetCache() {
		lastServer = null;
	}
}
