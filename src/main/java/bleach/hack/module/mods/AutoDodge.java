package bleach.hack.module.mods;

import bleach.hack.event.events.EventTick;
import bleach.hack.event.events.EventWorldRender;
import bleach.hack.module.Category;
import bleach.hack.module.Module;
import bleach.hack.setting.base.SettingMode;
import bleach.hack.setting.base.SettingSlider;
import bleach.hack.setting.base.SettingToggle;
import bleach.hack.utils.RenderUtils;
import bleach.hack.bleacheventbus.BleachSubscribe;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class AutoDodge extends Module
{
    private final List<BlockPos> poses = new ArrayList<>();
    public Vec3d prevPos;
    private double[] rPos;

    public AutoDodge()
    {
        super("AutoDodge", KEY_UNBOUND, Category.PLAYER, "Automatically dodge obstacles",
                new SettingSlider("Range: ", 1.0D, 25.0D, 5.0D, 0)
        );
    }

    @BleachSubscribe
    public void onTick(EventTick event) {
        BlockPos player = mc.player.getBlockPos().north((int) this.getSettings().get(0).asSlider().getValue());
        if (this.mc.world.getBlockState(player).getBlock() != Blocks.AIR) {
            if (this.mc.world.getBlockState(mc.player.getBlockPos().east(1).north(1)).getBlock() == Blocks.AIR) {
                mc.player.setVelocity(0.1, mc.player.getVelocity().y, 0);
            } else if (this.mc.world.getBlockState(mc.player.getBlockPos().west(1).north(1)).getBlock() == Blocks.AIR) {
                mc.player.setVelocity(-0.1, mc.player.getVelocity().y, 0);
            }
        }
    }
}