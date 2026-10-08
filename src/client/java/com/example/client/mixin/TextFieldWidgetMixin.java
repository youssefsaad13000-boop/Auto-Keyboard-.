package com.example.client.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for TextFieldWidget to automatically show/hide Android keyboard via TouchController.
 *
 * When a Minecraft text field gains focus, this mixin calls TouchController's InputManager
 * to show the system keyboard. When focus is lost, it hides the keyboard.
 *
 * This implementation uses safe runtime reflection to access TouchController's InputManager,
 * ensuring the mod gracefully handles cases where TouchController is not installed.
 */
@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {

    @Inject(method = "setFocused", at = @At("HEAD"))
    private void autoKeyboard$onSetFocused(boolean focused, CallbackInfo ci) {
        if (focused) {
            tryShowKeyboard();
        } else {
            tryHideKeyboard();
        }
    }

    /**
     * Attempts to show the Android keyboard via TouchController's InputManager.
     * Uses safe reflection to access InputManager at runtime.
     * If TouchController is not installed or InputManager is unavailable, silently returns.
     */
    private static void tryShowKeyboard() {
        try {
            // Try to get TouchController's InputManager class
            Class<?> inputManagerClass = Class.forName(
                "top.fifthlight.touchcontroller.common.input.InputManager"
            );

            // Get the INSTANCE singleton
            Object inputManager = inputManagerClass.getField("INSTANCE").get(null);

            if (inputManager != null) {
                // Call tryShowKeyboard() method
                inputManagerClass.getMethod("tryShowKeyboard").invoke(inputManager);
            }
        } catch (ClassNotFoundException e) {
            // TouchController not installed - graceful fallback
            // This is expected behavior when running without TouchController
        } catch (Throwable e) {
            // Any other error: silently ignore to avoid breaking the game
            // This ensures the game works even if something goes wrong with keyboard handling
        }
    }

    /**
     * Attempts to hide the Android keyboard via TouchController's InputManager.
     * Uses safe reflection to access InputManager at runtime.
     * If TouchController is not installed or InputManager is unavailable, silently returns.
     */
    private static void tryHideKeyboard() {
        try {
            // Try to get TouchController's InputManager class
            Class<?> inputManagerClass = Class.forName(
                "top.fifthlight.touchcontroller.common.input.InputManager"
            );

            // Get the INSTANCE singleton
            Object inputManager = inputManagerClass.getField("INSTANCE").get(null);

            if (inputManager != null) {
                // Call tryHideKeyboard() method
                inputManagerClass.getMethod("tryHideKeyboard").invoke(inputManager);
            }
        } catch (ClassNotFoundException e) {
            // TouchController not installed - graceful fallback
            // This is expected behavior when running without TouchController
        } catch (Throwable e) {
            // Any other error: silently ignore to avoid breaking the game
            // This ensures the game works even if something goes wrong with keyboard handling
        }
    }
}
