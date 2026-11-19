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

import java.util.ArrayList;
import java.util.List;

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
        return decodeAll(what) != null;
    }

    /**
     * 本をポイントへ分解して受け入れる。
     * 複数エンチャントが付いた本は、含まれる全エンチャントへポイントを配分する
     * (Apothic の depositBook と同じ振る舞い)。
     */
    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        List<Decoded> parts = decodeAll(what);
        if (parts == null || amount <= 0) return 0;

        int maxPoints = LibrarySnapshot.capOf(library);

        // 1冊分の余地があるエンチャントが1つでもあれば、その冊数まで受け入れる。
        // (分解先の一部が満杯でも、他のエンチャントにポイントが入るため)
        long capacity = 0;
        boolean raisesLevel = false;
        for (Decoded part : parts) {
            long cost = EnchLibraryTile.levelToPoints(part.level());
            if (cost <= 0) continue;
            long room = Math.max(0L, ((long) maxPoints - pointsOf(part)) / cost);
            capacity = Math.max(capacity, room);
            raisesLevel |= raisesLevelOf(part);
        }

        // ポイントが満杯でも、Apothic の depositBook は「預けたことのある最大レベル」を
        // 上げるために本を消費する。その場合は1冊だけ受け入れる。
        long accepted = capacity > 0 ? Math.min(amount, capacity) : (raisesLevel ? 1 : 0);

        if (accepted > 0 && mode == Actionable.MODULATE) {
            Object2IntMap<Holder<Enchantment>> points = library.getPointsMap();
            Object2IntMap<Holder<Enchantment>> maxLevels = library.getLevelsMap();
            for (Decoded part : parts) {
                long cost = EnchLibraryTile.levelToPoints(part.level());
                if (cost <= 0) continue;
                points.put(part.enchantment(), (int) Math.min(maxPoints, pointsOf(part) + accepted * cost));
                maxLevels.put(part.enchantment(),
                    Math.min(library.getEnchantmentCap(part.enchantment()),
                        Math.max(maxLevels.getInt(part.enchantment()), part.level())));
            }
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
        Decoded book = decodeSingle(what);
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

    private int pointsOf(Decoded part) {
        return library.getPointsMap().getInt(part.enchantment());
    }

    /** まだ預けたことのないレベルなら、1冊使って解放レベルを上げられる。 */
    private boolean raisesLevelOf(Decoded part) {
        int known = library.getLevelsMap().getInt(part.enchantment());
        return part.level() > known && known < library.getEnchantmentCap(part.enchantment());
    }

    /** エンチャント本なら含まれるエンチャント(レベル1以上)を全部返す。本でなければ null。 */
    private List<Decoded> decodeAll(AEKey key) {
        if (!(key instanceof AEItemKey item) || !item.getReadOnlyStack().is(Items.ENCHANTED_BOOK)) return null;
        var enchants = EnchantmentHelper.getEnchantmentsForCrafting(item.getReadOnlyStack());
        if (enchants.isEmpty()) return null;
        List<Decoded> parts = new ArrayList<>(enchants.size());
        for (var entry : enchants.entrySet()) {
            if (entry.getIntValue() <= 0) continue;
            parts.add(new Decoded(entry.getKey(), entry.getIntValue()));
        }
        return parts.isEmpty() ? null : parts;
    }

    /** 取り出しは、一覧に出している単一エンチャント本のキーだけを対象にする。 */
    private Decoded decodeSingle(AEKey key) {
        if (!(key instanceof AEItemKey item) || !item.getReadOnlyStack().is(Items.ENCHANTED_BOOK)) return null;
        var enchants = EnchantmentHelper.getEnchantmentsForCrafting(item.getReadOnlyStack());
        if (enchants.size() != 1) return null;
        var entry = enchants.entrySet().iterator().next();
        return new Decoded(entry.getKey(), entry.getIntValue());
    }

    private record Decoded(Holder<Enchantment> enchantment, int level) {}
}
