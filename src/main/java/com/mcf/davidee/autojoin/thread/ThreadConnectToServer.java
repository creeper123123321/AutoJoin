package com.mcf.davidee.autojoin.thread;

import java.net.InetAddress;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;

import com.mcf.davidee.autojoin.AJLoginHandler;
import com.mcf.davidee.autojoin.AutoJoin;
import com.mcf.davidee.autojoin.ServerInfo;
import com.mcf.davidee.autojoin.gui.AutoJoinScreen;

public class ThreadConnectToServer extends Thread {
	
	private final AutoJoinScreen screen;
	private final Minecraft mc;
	private final ServerInfo info;
	
	public ThreadConnectToServer(AutoJoinScreen screen, ServerInfo info) {
		this.screen = screen;
		this.mc = Minecraft.getMinecraft();
		this.info = info;
	}
	
	public void run() {
		try {
			if (screen.isCancelled())
				return;
			NetworkManager manager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(info.ip), info.port, mc.gameSettings.isUsingNativeTransport());
			//TODO change this back to the AJ screen?
			manager.setNetHandler(new AJLoginHandler(manager, mc, null));
			manager.sendPacket(new C00Handshake(info.ip, info.port, EnumConnectionState.LOGIN));
            manager.sendPacket(new CPacketLoginStart(mc.getSession().getProfile()));
            screen.setManager(manager);
		}
		catch(Exception e) {
			if (!screen.isCancelled())
				screen.connectError(e.getMessage());
		}
	}
}
