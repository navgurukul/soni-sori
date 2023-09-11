package org.navgurukul.webide.extensions;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 2, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u001a7\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0000\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007\u00a2\u0006\u0002\u0010\t\u001a\u0018\u0010\n\u001a\u00020\u0001*\u00020\u00022\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b\u001a\u0012\u0010\f\u001a\u00020\b*\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005\u001a?\u0010\u000f\u001a\u00020\u0001*\u00020\b2\b\b\u0001\u0010\u0010\u001a\u00020\u00052\b\b\u0002\u0010\u0011\u001a\u00020\u00052\u0019\b\u0002\u0010\u0012\u001a\u0013\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00010\u0007\u00a2\u0006\u0002\b\u0013H\u0086\b\u00f8\u0001\u0000\u001a=\u0010\u000f\u001a\u00020\u0001*\u00020\b2\u0006\u0010\u0010\u001a\u00020\u00032\b\b\u0002\u0010\u0011\u001a\u00020\u00052\u0019\b\u0002\u0010\u0012\u001a\u0013\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00010\u0007\u00a2\u0006\u0002\b\u0013H\u0086\b\u00f8\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006\u0014"}, d2 = {"action", "", "Lcom/google/android/material/snackbar/Snackbar;", "", "color", "", "listener", "Lkotlin/Function1;", "Landroid/view/View;", "(Lcom/google/android/material/snackbar/Snackbar;Ljava/lang/String;Ljava/lang/Integer;Lkotlin/jvm/functions/Function1;)V", "callback", "Lkotlin/Function0;", "inflate", "Landroid/view/ViewGroup;", "layoutId", "snack", "message", "length", "f", "Lkotlin/ExtensionFunctionType;", "webIDE_debug"})
public final class ViewKt {
    
    @org.jetbrains.annotations.NotNull()
    public static final android.view.View inflate(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup $this$inflate, int layoutId) {
        return null;
    }
    
    public static final void snack(@org.jetbrains.annotations.NotNull()
    android.view.View $this$snack, @org.jetbrains.annotations.NotNull()
    java.lang.String message, int length, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.google.android.material.snackbar.Snackbar, kotlin.Unit> f) {
    }
    
    public static final void snack(@org.jetbrains.annotations.NotNull()
    android.view.View $this$snack, @androidx.annotation.StringRes()
    int message, int length, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.google.android.material.snackbar.Snackbar, kotlin.Unit> f) {
    }
    
    public static final void action(@org.jetbrains.annotations.NotNull()
    com.google.android.material.snackbar.Snackbar $this$action, @org.jetbrains.annotations.NotNull()
    java.lang.String action, @org.jetbrains.annotations.Nullable()
    java.lang.Integer color, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super android.view.View, kotlin.Unit> listener) {
    }
    
    public static final void callback(@org.jetbrains.annotations.NotNull()
    com.google.android.material.snackbar.Snackbar $this$callback, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> callback) {
    }
}