package bleach.hack.module.mods;

import bleach.hack.module.Category;
import bleach.hack.module.Module;
import net.minecraft.container.SlotActionType;
import net.minecraft.item.Items;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.server.network.packet.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoTotem extends Module {

	public AutoTotem() {
		super("AutoTotem", -1, Category.COMBAT, "Automatically equips totems.", null);
	}
	
	public void onUpdate() {
		if(mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;
		
		/*Inventory*/
		for(int i = 9; i < 44; i++) {
			if(mc.player.inventory.getInvStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
				mc.player.container.onSlotClick(0, i, SlotActionType.PICKUP, mc.player);
				mc.player.container.onSlotClick(0, 45, SlotActionType.PICKUP, mc.player);
				return;
			}
		}
		
		/*Hotbar*/
		for(int i = 0; i < 8; i++) {
			if(mc.player.inventory.getInvStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
				//int oldSlot = mc.player.inventory.currentItem;
				mc.player.inventory.selectedSlot = i;
				mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(
						Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, Direction.DOWN));
				//mc.player.inventory.currentItem = oldSlot;
				return;
			}
		}
	}

}