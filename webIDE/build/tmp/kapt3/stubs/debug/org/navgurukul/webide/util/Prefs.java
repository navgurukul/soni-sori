package org.navgurukul.webide.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J!\u0010\u0007\u001a\u00020\b*\u00020\u00042\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\b0\nH\u0082\bJ4\u0010\f\u001a\u0004\u0018\u0001H\r\"\n\b\u0000\u0010\r\u0018\u0001*\u00020\u0001*\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u0001H\rH\u0086\n\u00a2\u0006\u0002\u0010\u0011J\u001f\u0010\u0012\u001a\u00020\b*\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u0086\u0002\u00a8\u0006\u0014"}, d2 = {"Lorg/navgurukul/webide/util/Prefs;", "", "()V", "defaultPrefs", "Landroid/content/SharedPreferences;", "context", "Landroid/content/Context;", "edit", "", "operation", "Lkotlin/Function1;", "Landroid/content/SharedPreferences$Editor;", "get", "T", "key", "", "defaultValue", "(Landroid/content/SharedPreferences;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", "set", "value", "webIDE_debug"})
public final class Prefs {
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.util.Prefs INSTANCE = null;
    
    private Prefs() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.content.SharedPreferences defaultPrefs(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    private final void edit(android.content.SharedPreferences $this$edit, kotlin.jvm.functions.Function1<? super android.content.SharedPreferences.Editor, kotlin.Unit> operation) {
    }
    
    public final void set(@org.jetbrains.annotations.NotNull()
    android.content.SharedPreferences $this$set, @org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.Nullable()
    java.lang.Object value) {
    }
}