package bleach.hack.module.mods;

import bleach.hack.BleachHack;
import bleach.hack.event.events.EventSendPacket;
import bleach.hack.event.events.EventTick;
import bleach.hack.module.Category;
import bleach.hack.module.Module;
import bleach.hack.module.ModuleManager;
import bleach.hack.setting.base.SettingMode;
import bleach.hack.setting.base.SettingToggle;
import bleach.hack.utils.BleachLogger;
import bleach.hack.bleacheventbus.BleachSubscribe;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoAnvil extends Module {

    public AutoAnvil() {
        super("AutoAnvil", KEY_UNBOUND, Category.COMBAT, "automatically air places anvil to surround");
    }

    @BleachSubscribe
    public void onTick(EventTick event) {
        assert mc.player != null;
        assert mc.interactionManager != null;
        int blockSlot = -1;
        PlayerEntity target = null;
        for(int i = 0; i < 9; i++){
            if (mc.player.getInventory().getStack(i).getItem() == Blocks.ANVIL.asItem()){
                blockSlot = i;
                break;
            }
        }
        if (blockSlot == -1) return;
        for(PlayerEntity player : mc.world.getPlayers()){
            target = mc.player;
        }
        int prevSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = blockSlot;
        BlockPos targetPos = target.getBlockPos().up();
        if(mc.world.getBlockState(targetPos.add(0, 1, 0)).getMaterial().isReplaceable()){
            mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(0, 1, 0), Direction.UP, targetPos.add(0, 1, 0), false));
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        mc.player.getInventory().selectedSlot = prevSlot;
        ModuleManager.getModule(AutoAnvil.class).toggle();
    }
}
