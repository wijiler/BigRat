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
package bleach.hack.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemContentUtils {

    public static List<ItemStack> getItemsInContainer(ItemStack item) {
        List<ItemStack> items = new ArrayList<>(Collections.nCopies(27, new ItemStack(Items.AIR)));
        NbtCompound nbt = item.getNbt();

        if (nbt != null && nbt.contains("BlockEntityTag")) {
            NbtCompound nbt2 = nbt.getCompound("BlockEntityTag");
            if (nbt2.contains("Items")) {
                NbtList nbt3 = (NbtList) nbt2.get("Items");
                for (int i = 0; i < nbt3.size(); i++) {
                    items.set(nbt3.getCompound(i).getByte("Slot"), ItemStack.fromNbt(nbt3.getCompound(i)));
                }
            }
        }

        return items;
    }

    public static List<List<String>> getTextInBook(ItemStack item) {
        List<String> pages = new ArrayList<>();
        NbtCompound nbt = item.getNbt();

        if (nbt != null && nbt.contains("pages")) {
            NbtList nbt2 = nbt.getList("pages", 8);
            for (int i = 0; i < nbt2.size(); i++) pages.add(nbt2.getString(i));
        }

        List<List<String>> finalPages = new ArrayList<>();

        for (String s : pages) {
            String buffer = "";
            List<String> pageBuffer = new ArrayList<>();

            for (char c : s.toCharArray()) {
                if (MinecraftClient.getInstance().textRenderer.getWidth(buffer) > 114 || buffer.endsWith("\n")) {
                    pageBuffer.add(buffer.replace("\n", ""));
                    buffer = "";
                }

                buffer += c;
            }
            pageBuffer.add(buffer);
            finalPages.add(pageBuffer);
        }

        return finalPages;
    }
}
