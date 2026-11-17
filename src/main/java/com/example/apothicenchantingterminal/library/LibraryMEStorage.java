package com.example.apothicenchantingterminal.library;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryTile;
import dev.shadowsoffire.placebo.network.VanillaPacketDispatcher;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

/**
 * AE2上で図書館のポイントをエンチャント本として公開する仮想ストレージ。
 * Applied Apothic Enchantingのコードには依存せず、Apothicの公開マップ/APIだけを使う。
 */
public final class LibraryMEStorage implements MEStorage {
    private final EnchLibraryTile library;

    public LibraryMEStorage(EnchLibraryTile library) {
        this.library = library;
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return decode(what) != null;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        Decoded book = decode(what);
        if (book == null || amount <= 0) return 0;

        Holder<Enchantment> ench = book.enchantment();
        int bookLevel = book.level();
        long cost = EnchLibraryTile.levelToPoints(bookLevel);
        if (cost <= 0) return 0;

        Object2IntMap<Holder<Enchantment>> points = library.getPointsMap();
        Object2IntMap<Holder<Enchantment>> maxLevels = library.getLevelsMap();

        int current = points.getInt(ench);
        // 図書館の総ポイント上限(基本=16Lv相当、エンダー=31Lv相当)。
        // 預け入れの上限は「解放済み最大レベル」ではなく、この総量で決まる。
        int maxPoints = LibrarySnapshot.capOf(library);
        int known = maxLevels.getInt(ench);
        int cap = library.getEnchantmentCap(ench);

        // 残りポイントで受け入れられる冊数
        long byPoints = Math.max(0L, ((long) maxPoints - current) / cost);
        // Apothic の depositBook は、ポイントが満杯でも「預けたことのある最大レベル」を
        // 上げるために本を消費する。その場合だけ 1 冊受け入れる。
        long accepted = byPoints > 0
            ? Math.min(amount, byPoints)
            : (bookLevel > known && known < cap ? 1 : 0);

        if (accepted > 0 && mode == Actionable.MODULATE) {
            points.put(ench, (int) Math.min(maxPoints, current + accepted * cost));
            maxLevels.put(ench, Math.min(cap, Math.max(known, bookLevel)));
            library.setChanged();
            Level level = library.getLevel();
            if (level != null && !level.isClientSide) {
                // 図書館の画面を開いている人にも反映する(Apothic の depositBook と同じ)
                VanillaPacketDispatcher.dispatchTEToNearbyPlayers(library);
            }
        }
        return accepted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        Decoded book = decode(what);
        if (book == null || amount <= 0) return 0;
        // 図書館がまだ知らないレベル(預けたことのないレベル)は取り出せない(Apothic の canExtract と同じ)
        if (book.level() > library.getMax(book.enchantment())) return 0;
        long cost = EnchLibraryTile.levelToPoints(book.level());
        long available = library.getPointsMap().getInt(book.enchantment()) / cost;
        long extracted = Math.min(amount, available);
        if (extracted > 0 && mode == Actionable.MODULATE) {
            int remaining = Math.toIntExact(library.getPointsMap().getInt(book.enchantment()) - extracted * cost);
            library.getPointsMap().put(book.enchantment(), remaining);
            library.setChanged();
        }
        return extracted;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        var registry = library.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        for (var entry : library.getPointsMap().object2IntEntrySet()) {
            Holder<Enchantment> ench = entry.getKey();
            int max = library.getMax(ench);
            for (int level = 1; level <= max; level++) {
                long count = entry.getIntValue() / EnchLibraryTile.levelToPoints(level);
                if (count <= 0) continue;
                ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                mutable.set(ench, level);
                EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
                AEItemKey key = AEItemKey.of(stack);
                if (key != null) out.add(key, count);
            }
        }
    }

    @Override
    public net.minecraft.network.chat.Component getDescription() {
        return net.minecraft.network.chat.Component.translatable("container.apothic_enchanting.library");
    }

    private Decoded decode(AEKey key) {
        if (!(key instanceof AEItemKey item) || !item.getReadOnlyStack().is(Items.ENCHANTED_BOOK)) return null;
        var enchants = EnchantmentHelper.getEnchantmentsForCrafting(item.getReadOnlyStack());
        if (enchants.size() != 1) return null;
        var entry = enchants.entrySet().iterator().next();
        return new Decoded(entry.getKey(), entry.getIntValue());
    }

    private record Decoded(Holder<Enchantment> enchantment, int level) {}
}
