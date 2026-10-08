package com.example.client.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {

    @Inject(method = "onClick", at = @At("HEAD"))
    private void onWidgetClick(double mouseX, double mouseY, CallbackInfo ci) {
        showKeyboard();
    }

    @Inject(method = "setFocused", at = @At("HEAD"))
    private void onFocusChange(boolean focused, CallbackInfo ci) {
        if (focused) {
            showKeyboard();
        }
    }

    private void showKeyboard() {
        try {
            Class<?> activityClass = Class.forName("net.kdt.pojavlaunch.MainActivity");
            Object activityInstance = activityClass.getField("mInstance").get(null);

            if (activityInstance != null) {
                android.app.Activity activity = (android.app.Activity) activityInstance;
                activity.runOnUiThread(() -> {
                    android.view.inputmethod.InputMethodManager imm = 
                        (android.view.inputmethod.InputMethodManager) activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    
                    if (imm != null) {
                        imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, 0);
                    }
                });
            }
        } catch (Exception ignored) {}
    }
}
