package com.example.client.mixin;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {

    @Inject(method = "onClick", at = @At("HEAD"))
    private void autoKeyboard$onClick(
            double mouseX,
            double mouseY,
            CallbackInfo ci
    ) {
        showKeyboard();
    }

    @Inject(method = "setFocused", at = @At("HEAD"))
    private void autoKeyboard$onFocus(boolean focused, CallbackInfo ci) {
        if (focused) {
            showKeyboard();
        }
    }

    private void showKeyboard() {
        try {
            Class<?> activityClass =
                    Class.forName("net.kdt.pojavlaunch.MainActivity");

            Object activity =
                    activityClass.getField("mInstance").get(null);

            if (activity == null) {
                return;
            }

            activity.getClass()
                    .getMethod("runOnUiThread", Runnable.class)
                    .invoke(activity, (Runnable) () -> {
                        try {
                            Object imm = activity.getClass()
                                    .getMethod(
                                            "getSystemService",
                                            String.class
                                    )
                                    .invoke(activity, "input_method");

                            if (imm != null) {
                                imm.getClass()
                                        .getMethod(
                                                "toggleSoftInput",
                                                int.class,
                                                int.class
                                        )
                                        .invoke(imm, 2, 0);
                            }
                        } catch (Throwable ignored) {
                        }
                    });

        } catch (Throwable ignored) {
            // لا تجعل فشل الكيبورد يسبب Crash للعبة
        }
    }
}
