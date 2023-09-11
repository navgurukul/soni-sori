package org.navgurukul.webide.util.editor;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0011B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J.\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lorg/navgurukul/webide/util/editor/Highlighter;", "", "()V", "currentThread", "Lorg/navgurukul/webide/util/editor/Highlighter$HighlightThread;", "run", "", "context", "Landroid/content/Context;", "codeView", "Lorg/navgurukul/webide/ui/widget/Editor;", "editable", "Landroid/text/Editable;", "fileEnding", "", "darkTheme", "", "HighlightThread", "webIDE_debug"})
public final class Highlighter {
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.util.editor.Highlighter INSTANCE = null;
    private static org.navgurukul.webide.util.editor.Highlighter.HighlightThread currentThread;
    
    private Highlighter() {
        super();
    }
    
    public final void run(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.widget.Editor codeView, @org.jetbrains.annotations.NotNull()
    android.text.Editable editable, @org.jetbrains.annotations.NotNull()
    java.lang.String fileEnding, boolean darkTheme) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0016\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\tH\u0002J\b\u0010\u0010\u001a\u00020\u000eH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lorg/navgurukul/webide/util/editor/Highlighter$HighlightThread;", "Ljava/lang/Thread;", "codeView", "Lorg/navgurukul/webide/ui/widget/Editor;", "defs", "Ljava/util/ArrayList;", "Lorg/navgurukul/webide/util/json/ColorReader$ColorDef;", "Lkotlin/collections/ArrayList;", "editable", "Landroid/text/Editable;", "darkTheme", "", "(Lorg/navgurukul/webide/ui/widget/Editor;Ljava/util/ArrayList;Landroid/text/Editable;Z)V", "clearSpans", "", "e", "run", "webIDE_debug"})
    public static final class HighlightThread extends java.lang.Thread {
        private org.navgurukul.webide.ui.widget.Editor codeView;
        private java.util.ArrayList<org.navgurukul.webide.util.json.ColorReader.ColorDef> defs;
        private android.text.Editable editable;
        private boolean darkTheme;
        
        public HighlightThread(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.ui.widget.Editor codeView, @org.jetbrains.annotations.NotNull()
        java.util.ArrayList<org.navgurukul.webide.util.json.ColorReader.ColorDef> defs, @org.jetbrains.annotations.NotNull()
        android.text.Editable editable, boolean darkTheme) {
            super();
        }
        
        @java.lang.Override()
        public void run() {
        }
        
        private final void clearSpans(android.text.Editable e) {
        }
    }
}