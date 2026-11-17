package com.example.apothicenchantingterminal.integration;

import com.example.apothicenchantingterminal.menu.LibraryTerminalMenu;

import appeng.api.networking.security.IActionHost;

/**
 * AE2WTLib が読み込まれているときだけ、統合クラスを反射で呼び出すための橋渡し。
 * <p>
 * このクラス自身は AE2WTLib の型を一切参照しない。統合クラスには ItemWT / WTMenuHost などが
 * 出てくるので、AE2WTLib が無い環境で(たとえメソッドを呼ばなくても)クラスを参照しただけで
 * NoClassDefFoundError になる。呼び出し側は必ずこのクラスを経由すること。
 */
public final class WtlibBridge {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("apothic_enchanting_terminal");

    private static final String INTEGRATION = "com.example.apothicenchantingterminal.integration.WtlibIntegration";
    private static final String MENU_SLOTS = "com.example.apothicenchantingterminal.integration.WtlibMenuSlots";

    private WtlibBridge() {}

    /** 端末アイテムの登録と、AE2WTLibへの端末の登録。 */
    public static void init() {
        invoke(INTEGRATION, "init", new Class<?>[0]);
    }

    /** ワイヤレス端末のホストに、アップグレードなどのスロットを追加する。 */
    public static void installWtlibSlots(LibraryTerminalMenu menu, IActionHost host) {
        try {
            invoke(MENU_SLOTS, "install", new Class<?>[] { LibraryTerminalMenu.class, IActionHost.class }, menu, host);
        } catch (RuntimeException e) {
            // アップグレードのスロットが無くても端末自体は使えるので、ここでは落とさない
            LOGGER.warn("Could not add AE2WTLib slots to the library terminal", e);
        }
    }

    private static void invoke(String className, String method, Class<?>[] parameterTypes, Object... args) {
        try {
            Class.forName(className).getMethod(method, parameterTypes).invoke(null, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to call AE2WTLib integration " + className + "#" + method, e);
        }
    }
}
