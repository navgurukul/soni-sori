package org.navgurukul.webide.ui.widget;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0088\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\r\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001:\u0002FGB\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J(\u00104\u001a\u00020\u001f2\u0006\u00105\u001a\u00020\u001f2\u0006\u00106\u001a\u0002072\u0006\u00108\u001a\u00020\n2\u0006\u00109\u001a\u00020\nH\u0002J\b\u0010:\u001a\u00020;H\u0002J\u0010\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020=H\u0002J\u0010\u0010?\u001a\u00020;2\u0006\u0010>\u001a\u00020=H\u0002J\u0010\u0010@\u001a\u00020;2\u0006\u0010A\u001a\u00020BH\u0014J\u000e\u0010C\u001a\u00020;2\u0006\u0010D\u001a\u00020\u001fJ\b\u0010E\u001a\u00020;H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\u00020\n8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0010\u001a\u00020\u0011X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b \u0010!R\u0010\u0010\"\u001a\u0004\u0018\u00010\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010#\u001a\u0004\u0018\u00010$X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b\'\u0010(R\u0016\u0010)\u001a\n +*\u0004\u0018\u00010*0*X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010,\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b-\u0010\f\"\u0004\b.\u0010/R\u000e\u00100\u001a\u000201X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u00102\u001a\u000203X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006H"}, d2 = {"Lorg/navgurukul/webide/ui/widget/Editor;", "Landroidx/appcompat/widget/AppCompatMultiAutoCompleteTextView;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "colors", "", "currentCursorLine", "", "getCurrentCursorLine", "()I", "currentLine", "darkTheme", "", "fileEnding", "", "getFileEnding", "()Ljava/lang/String;", "setFileEnding", "(Ljava/lang/String;)V", "fileModified", "hasLineNumbers", "lineDiff", "lineRect", "Landroid/graphics/Rect;", "lineShadowPaint", "Landroid/graphics/Paint;", "lines", "", "", "getLines", "()Ljava/util/List;", "numberPaint", "onTextChangedListener", "Lorg/navgurukul/webide/ui/widget/Editor$OnTextChangedListener;", "getOnTextChangedListener", "()Lorg/navgurukul/webide/ui/widget/Editor$OnTextChangedListener;", "setOnTextChangedListener", "(Lorg/navgurukul/webide/ui/widget/Editor$OnTextChangedListener;)V", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "updateDelay", "getUpdateDelay", "setUpdateDelay", "(I)V", "updateHandler", "Landroid/os/Handler;", "updateRunnable", "Ljava/lang/Runnable;", "autoIndent", "source", "dest", "Landroid/text/Spanned;", "dstart", "dend", "cancelUpdate", "", "highlight", "Landroid/text/Editable;", "e", "highlightWithoutChange", "onDraw", "canvas", "Landroid/graphics/Canvas;", "setTextHighlighted", "text", "setupAutoComplete", "EditorCallback", "OnTextChangedListener", "webIDE_debug"})
public final class Editor extends androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView {
    public java.lang.String fileEnding;
    private final android.os.Handler updateHandler = null;
    @org.jetbrains.annotations.Nullable()
    private org.navgurukul.webide.ui.widget.Editor.OnTextChangedListener onTextChangedListener;
    private int updateDelay = 3000;
    private int currentLine = 0;
    private int lineDiff = 0;
    private boolean fileModified = true;
    private android.graphics.Rect lineRect;
    private android.graphics.Paint numberPaint;
    private android.graphics.Paint lineShadowPaint;
    private int[] colors;
    private final java.lang.Runnable updateRunnable = null;
    private boolean hasLineNumbers = false;
    private boolean darkTheme = false;
    private android.content.SharedPreferences prefs;
    private java.util.HashMap _$_findViewCache;
    
    public Editor(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFileEnding() {
        return null;
    }
    
    public final void setFileEnding(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final org.navgurukul.webide.ui.widget.Editor.OnTextChangedListener getOnTextChangedListener() {
        return null;
    }
    
    public final void setOnTextChangedListener(@org.jetbrains.annotations.Nullable()
    org.navgurukul.webide.ui.widget.Editor.OnTextChangedListener p0) {
    }
    
    public final int getUpdateDelay() {
        return 0;
    }
    
    public final void setUpdateDelay(int p0) {
    }
    
    private final java.util.List<java.lang.CharSequence> getLines() {
        return null;
    }
    
    private final int getCurrentCursorLine() {
        return 0;
    }
    
    public final void setTextHighlighted(@org.jetbrains.annotations.NotNull()
    java.lang.CharSequence text) {
    }
    
    private final void cancelUpdate() {
    }
    
    private final void highlightWithoutChange(android.text.Editable e) {
    }
    
    private final android.text.Editable highlight(android.text.Editable e) {
        return null;
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    private final java.lang.CharSequence autoIndent(java.lang.CharSequence source, android.text.Spanned dest, int dstart, int dend) {
        return null;
    }
    
    private final void setupAutoComplete() {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lorg/navgurukul/webide/ui/widget/Editor$OnTextChangedListener;", "", "onTextChanged", "", "text", "", "webIDE_debug"})
    public static abstract interface OnTextChangedListener {
        
        public abstract void onTextChanged(@org.jetbrains.annotations.NotNull()
        java.lang.String text);
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0018\u0010\r\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\nH\u0016J\u0018\u0010\u0012\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016R\u0014\u0010\u0003\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0013"}, d2 = {"Lorg/navgurukul/webide/ui/widget/Editor$EditorCallback;", "Landroid/view/ActionMode$Callback;", "(Lorg/navgurukul/webide/ui/widget/Editor;)V", "selectedString", "", "getSelectedString", "()Ljava/lang/String;", "onActionItemClicked", "", "mode", "Landroid/view/ActionMode;", "item", "Landroid/view/MenuItem;", "onCreateActionMode", "menu", "Landroid/view/Menu;", "onDestroyActionMode", "", "onPrepareActionMode", "webIDE_debug"})
    final class EditorCallback implements android.view.ActionMode.Callback {
        
        public EditorCallback() {
            super();
        }
        
        private final java.lang.String getSelectedString() {
            return null;
        }
        
        @java.lang.Override()
        public boolean onCreateActionMode(@org.jetbrains.annotations.NotNull()
        android.view.ActionMode mode, @org.jetbrains.annotations.NotNull()
        android.view.Menu menu) {
            return false;
        }
        
        @java.lang.Override()
        public boolean onPrepareActionMode(@org.jetbrains.annotations.NotNull()
        android.view.ActionMode mode, @org.jetbrains.annotations.NotNull()
        android.view.Menu menu) {
            return false;
        }
        
        @java.lang.Override()
        public boolean onActionItemClicked(@org.jetbrains.annotations.NotNull()
        android.view.ActionMode mode, @org.jetbrains.annotations.NotNull()
        android.view.MenuItem item) {
            return false;
        }
        
        @java.lang.Override()
        public void onDestroyActionMode(@org.jetbrains.annotations.NotNull()
        android.view.ActionMode mode) {
        }
    }
}