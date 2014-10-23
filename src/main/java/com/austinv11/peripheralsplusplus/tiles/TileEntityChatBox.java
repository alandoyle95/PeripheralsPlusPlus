package com.austinv11.peripheralsplusplus.tiles;

import com.austinv11.peripheralsplusplus.reference.Config;
import com.austinv11.peripheralsplusplus.utils.Logger;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.HashMap;
import java.util.List;

public class TileEntityChatBox extends TileEntity implements IPeripheral{

	public static String publicName = "tileEntityChatBox";
	private  String name = "tileEntityChatBox";
	private HashMap<IComputerAccess,Boolean> computers = new HashMap<IComputerAccess,Boolean>();

	public TileEntityChatBox (World w) {
		super();
		this.setWorldObj(w);
	}

	public String getName() {
		return name;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
	}

	@Override public void updateEntity() {
		//Logger.info("test2");
	}

	public void onChat(EntityPlayer player, String message) {
		for (IComputerAccess computer : computers.keySet())
			computer.queueEvent("chat", new Object[] {player.getCommandSenderName(), message});
	}

	public void onDeath(EntityPlayer player, DamageSource source) {
		String killer = null;
		if (source instanceof EntityDamageSource) {
			Entity ent = ((EntityDamageSource)source).getEntity();
			if (ent != null)
				killer = ent.getCommandSenderName();
		}
		for (IComputerAccess computer : computers.keySet())
			computer.queueEvent("chat", new Object[] {player.getCommandSenderName(), killer, source.damageType});
	}

	@Override
	public String getType() {
		return name;
	}

	@Override
	public String[] getMethodNames() {
		return new String[] {"say", "tell"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		try {
			if (Config.enableChatBox) {
				if (method == 0) {
					if (arguments.length < 1)
						throw new LuaException("Too few arguments");
					if (!(arguments[0] instanceof String))
						throw new LuaException("Bad argument #1 (expected string)");
					if (arguments.length > 1 && !(arguments[1] instanceof Double))
						throw new LuaException("Bad argument #2 (expected number)");
					if (arguments.length > 2 && !(arguments[2] instanceof Boolean))
						throw new LuaException("Bad argument #3 (expected boolean)");
					if (arguments.length > 3 && Config.logCoords && !(arguments[3] instanceof String)) {
						if (Config.logCoords) {
							throw new LuaException("Coordinate logging is enabled, disable this to enable naming");
						}
						throw new LuaException("Bad argument #4 (expected string)");
					}
					ChatComponentText message;
					if (Config.logCoords)
						message = new ChatComponentText("[#" + this.xCoord + "," + this.yCoord + "," + this.zCoord + "] " + (String) arguments[0]);
					if (!Config.logCoords && arguments.length > 3) {
						message = new ChatComponentText("[@] " + (String) arguments[0]);
					}else {
						message = new ChatComponentText("[" + (String) arguments[3] + "] " + (String) arguments[0]);
					}
					double range;
					if (Config.sayRange < 0) {
						range = Double.MAX_VALUE;
					}else {

					}
					if (arguments.length > 1)
						range = (Double) arguments[1];
					for (EntityPlayer player : (Iterable<EntityPlayer>) this.getWorldObj().playerEntities) {
						Vec3 playerPos = player.getPosition(1f);
						if (arguments.length > 2 && !((Boolean) arguments[2]))
							playerPos.yCoord = this.yCoord;
						if (playerPos.distanceTo(Vec3.createVectorHelper(this.xCoord, this.yCoord, this.zCoord)) > range)
							continue;
						player.addChatComponentMessage(message);
					}
					return new Object[]{true};
				} else if (method == 1) {
					if (arguments.length < 2)
						throw new LuaException("Too few arguments");
					if (!(arguments[0] instanceof String))
						throw new LuaException("Bad argument #1 (expected string)");
					if (!(arguments[1] instanceof String))
						throw new LuaException("Bad argument #2 (expected string)");
					else if (arguments.length > 2 && !(arguments[2] instanceof Double))
						throw new LuaException("Bad argument #3 (expected number)");
					else if (arguments.length > 3 && !(arguments[3] instanceof Boolean))
						throw new LuaException("Bad argument #4 (expected boolean)");
					if (arguments.length > 4 && Config.logCoords && !(arguments[4] instanceof String)) {
						if (Config.logCoords) {
							throw new LuaException("Coordinate logging is enabled, disable this to enable naming");
						}
						throw new LuaException("Bad argument #5 (expected string)");
					}
					ChatComponentText message;
					if (Config.logCoords)
						message = new ChatComponentText("[#" + this.xCoord + "," + this.yCoord + "," + this.zCoord + "] " + (String) arguments[1]);
					if (!Config.logCoords && arguments.length > 3) {
						message = new ChatComponentText("[@] " + (String) arguments[1]);
					}else {
						message = new ChatComponentText("[" + (String) arguments[3] + "] " + (String) arguments[1]);
					}
					double range = Double.MAX_VALUE;
					if (arguments.length > 2)
						range = (Double) arguments[2];
					EntityPlayer player = getPlayer((String) arguments[0]);
					if (player != null) {
						Vec3 playerPos = player.getPosition(1f);
						if (arguments.length > 3 && !((Boolean) arguments[3]))
							playerPos.yCoord = this.yCoord;
						if (playerPos.distanceTo(Vec3.createVectorHelper(this.xCoord, this.yCoord, this.zCoord)) > range)
							return new Object[]{false};
						player.addChatComponentMessage(message);
					} else {
						return new Object[]{false};
					}
				}
			}else {
				throw new LuaException("Chat boxes have been disabled");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Object[0];
	}

	private EntityPlayer getPlayer(String ign) {
		List<EntityPlayer> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		for (EntityPlayer p : players) {
			if (p.getCommandSenderName().equalsIgnoreCase(ign))
				return p;
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
		//Logger.info("yay!");
		if (computers.size() == 0)
			ChatListener.chatBoxMap.put(this,true);
		computers.put(computer, true);
	}

	@Override
	public void detach(IComputerAccess computer) {
		computers.remove(computer);
		if (computers.size() == 0)
			ChatListener.chatBoxMap.remove(this);
	}

	@Override
	public boolean equals(IPeripheral other) {//FIXME idk what I'm doing
		return (other == this);
	}

	public static class ChatListener {
		private static HashMap<TileEntityChatBox,Boolean> chatBoxMap = new HashMap<TileEntityChatBox, Boolean>();

		@SubscribeEvent
		public void onChat(ServerChatEvent event) {
			if (Config.enableChatBox) {
				for (TileEntityChatBox box : chatBoxMap.keySet()) {
					if (Config.readRange < 0 || Vec3.createVectorHelper(box.xCoord,box.yCoord,box.zCoord).distanceTo(event.player.getPosition(1.0f)) <= Config.readRange)
						box.onChat(event.player, event.message);
				}
			}
		}

		@SubscribeEvent
		public void onDeath(LivingDeathEvent event) {
			if (Config.enableChatBox) {
				if (event.entity instanceof EntityPlayer) {
					for (TileEntityChatBox box : chatBoxMap.keySet()) {
						if (Config.readRange < 0 || Vec3.createVectorHelper(box.xCoord,box.yCoord,box.zCoord).distanceTo(((EntityPlayer) event.entity).getPosition(1.0f)) <= Config.readRange)
							box.onDeath((EntityPlayer) event.entity, event.source);
					}
				}
			}
		}
	}
}