package com.example.apothicenchantingterminal.integration;

import com.example.apothicenchantingterminal.menu.LibraryTerminalMenu;

import appeng.api.networking.security.IActionHost;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;

/**
 * AE2WTLib の端末ホストを使っているときだけ、メニューに WTLib 用のスロットを追加する。
 * このクラスは AE2WTLib がある場合にしか読み込まれない(WtlibBridge 経由で呼ぶ)。
 */
public final class WtlibMenuSlots {

    private WtlibMenuSlots() {}

    public static void install(LibraryTerminalMenu menu, IActionHost host) {
        if (!(host instanceof WTMenuHost wtHost)) return;
        // WTLIBのアップグレードパネルは先頭スロットをシンギュラリティ枠として扱う。
        menu.addSemanticSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                wtHost.getSubInventory(WTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
        IUpgradeInventory upgrades = wtHost.getUpgrades();
        for (int i = 0; i < upgrades.size(); i++) {
            RestrictedInputSlot slot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.UPGRADES, upgrades, i);
            slot.setNotDraggable();
            menu.addSemanticSlot(slot, SlotSemantics.UPGRADE);
        }
    }
}
