/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/bleachhack-1.14/).
 * Copyright (c) 2019 Bleach.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bleach.hack.module.mods;

import bleach.hack.event.events.*;
import bleach.hack.module.Category;
import bleach.hack.module.Module;
import bleach.hack.setting.base.SettingSlider;
import bleach.hack.setting.base.SettingToggle;
import bleach.hack.utils.PlayerCopyEntity;
import bleach.hack.bleacheventbus.BleachSubscribe;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Freecam extends Module {

    private PlayerCopyEntity dummy;
    private double[] playerPos;
    private float[] playerRot;
    private Entity riding;

    private boolean prevFlying;
    private float prevFlySpeed;

    public Freecam() {
        super("Freecam", KEY_UNBOUND, Category.PLAYER, "Its freecam, you know what it does",
                new SettingSlider("Speed", 0, 3, 0.5, 2),
                new SettingToggle("Horse Inv", true));
    }

    @Override
    public void onEnable() {
        playerPos = new double[]{mc.player.getX(), mc.player.getY(), mc.player.getZ()};
        playerRot = new float[]{mc.player.getYaw(), mc.player.getPitch()};

        dummy = new PlayerCopyEntity();
        dummy.copyPositionAndRotation(mc.player);
        dummy.setBoundingBox(dummy.getBoundingBox().expand(0.1));

        dummy.spawn();

        if (mc.player.getVehicle() != null) {
            riding = mc.player.getVehicle();
            mc.player.getVehicle().removeAllPassengers();
        }

        if (mc.player.isSprinting()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
        }

        prevFlying = mc.player.getAbilities().flying;
        prevFlySpeed = mc.player.getAbilities().getFlySpeed();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        dummy.despawn();
        mc.player.noClip = false;
        mc.player.getAbilities().flying = prevFlying;
        mc.player.getAbilities().setFlySpeed(prevFlySpeed);

        mc.player.refreshPositionAndAngles(playerPos[0], playerPos[1], playerPos[2], playerRot[0], playerRot[1]);
        mc.player.setVelocity(Vec3d.ZERO);

        if (riding != null && mc.world.getEntityById(riding.getId()) != null) {
            mc.player.startRiding(riding);
        }

        super.onDisable();
    }

    @BleachSubscribe
    public void sendPacket(EventSendPacket event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket || event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.setCancelled(true);
        }
    }

    @BleachSubscribe
    public void onOpenScreen(EventOpenScreen event) {
        if (getSetting(1).asToggle().state && riding instanceof HorseBaseEntity) {
            if (event.getScreen() instanceof InventoryScreen) {
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
                event.setCancelled(true);
            }
        }
    }

    @BleachSubscribe
    public void onClientMove(EventClientMove event) {
        mc.player.noClip = true;
    }

    @BleachSubscribe
    public void onTick(EventTick event) {
        //mc.player.setSprinting(false);
        //mc.player.setVelocity(Vec3d.ZERO);
        mc.player.setOnGround(false);
        mc.player.getAbilities().setFlySpeed((float) (getSetting(0).asSlider().getValue() / 5));
        mc.player.getAbilities().flying = true;
    }

}
