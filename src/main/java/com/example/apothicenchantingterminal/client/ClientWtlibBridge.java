package com.example.apothicenchantingterminal.client;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * AE2WTLib があるときだけ、統合側の画面登録を反射で呼び出す。
 * このクラスは AE2WTLib の型を参照しないので、無い環境でも安全。
 */
public final class ClientWtlibBridge {

    private static final String CLIENT = "com.example.apothicenchantingterminal.integration.WtlibClient";

    private ClientWtlibBridge() {}

    public static void registerScreens(RegisterMenuScreensEvent event) {
        try {
            Class.forName(CLIENT)
                .getMethod("registerScreens", RegisterMenuScreensEvent.class)
                .invoke(null, event);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to call AE2WTLib client integration", e);
        }
    }
}
