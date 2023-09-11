package org.navgurukul.webide.ui.widget;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001\u001eB\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004B\u0017\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007B\u001f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u000eH\u0014J\"\u0010\u0019\u001a\u00020\u00132\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\b\u001a\u00020\tH\u0002J\b\u0010\u001a\u001a\u00020\u0013H\u0014J\b\u0010\u001b\u001a\u00020\u0013H\u0014J\u000e\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u0010R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lorg/navgurukul/webide/ui/widget/ScrimInsetsFrameLayout;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "defStyle", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "mInsetForeground", "Landroid/graphics/drawable/Drawable;", "mInsets", "Landroid/graphics/Rect;", "mOnInsetsCallback", "Lorg/navgurukul/webide/ui/widget/ScrimInsetsFrameLayout$OnInsetsCallback;", "mTempRect", "draw", "", "canvas", "Landroid/graphics/Canvas;", "fitSystemWindows", "", "insets", "init", "onAttachedToWindow", "onDetachedFromWindow", "setOnInsetsCallback", "onInsetsCallback", "OnInsetsCallback", "webIDE_debug"})
public final class ScrimInsetsFrameLayout extends android.widget.FrameLayout {
    private android.graphics.drawable.Drawable mInsetForeground;
    private android.graphics.Rect mInsets;
    private final android.graphics.Rect mTempRect = null;
    private org.navgurukul.webide.ui.widget.ScrimInsetsFrameLayout.OnInsetsCallback mOnInsetsCallback;
    private java.util.HashMap _$_findViewCache;
    
    public ScrimInsetsFrameLayout(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    public ScrimInsetsFrameLayout(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    public ScrimInsetsFrameLayout(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.util.AttributeSet attrs, int defStyle) {
        super(null);
    }
    
    private final void init(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
    }
    
    @java.lang.Override()
    protected boolean fitSystemWindows(@org.jetbrains.annotations.NotNull()
    android.graphics.Rect insets) {
        return false;
    }
    
    @java.lang.Override()
    public void draw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    @java.lang.Override()
    protected void onAttachedToWindow() {
    }
    
    @java.lang.Override()
    protected void onDetachedFromWindow() {
    }
    
    public final void setOnInsetsCallback(@org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.widget.ScrimInsetsFrameLayout.OnInsetsCallback onInsetsCallback) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lorg/navgurukul/webide/ui/widget/ScrimInsetsFrameLayout$OnInsetsCallback;", "", "onInsetsChanged", "", "insets", "Landroid/graphics/Rect;", "webIDE_debug"})
    public static abstract interface OnInsetsCallback {
        
        public abstract void onInsetsChanged(@org.jetbrains.annotations.NotNull()
        android.graphics.Rect insets);
    }
}