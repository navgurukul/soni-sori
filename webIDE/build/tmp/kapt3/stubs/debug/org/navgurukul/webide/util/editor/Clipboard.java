package org.navgurukul.webide.util.editor;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0015B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\u0014R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u0016"}, d2 = {"Lorg/navgurukul/webide/util/editor/Clipboard;", "", "()V", "currentFile", "Ljava/io/File;", "getCurrentFile", "()Ljava/io/File;", "setCurrentFile", "(Ljava/io/File;)V", "type", "Lorg/navgurukul/webide/util/editor/Clipboard$Type;", "getType", "()Lorg/navgurukul/webide/util/editor/Clipboard$Type;", "setType", "(Lorg/navgurukul/webide/util/editor/Clipboard$Type;)V", "update", "", "file", "t", "view", "Landroid/view/View;", "Type", "webIDE_debug"})
public final class Clipboard {
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.util.editor.Clipboard INSTANCE = null;
    @org.jetbrains.annotations.Nullable()
    private static java.io.File currentFile;
    @org.jetbrains.annotations.NotNull()
    private static org.navgurukul.webide.util.editor.Clipboard.Type type = org.navgurukul.webide.util.editor.Clipboard.Type.COPY;
    
    private Clipboard() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.io.File getCurrentFile() {
        return null;
    }
    
    public final void setCurrentFile(@org.jetbrains.annotations.Nullable()
    java.io.File p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final org.navgurukul.webide.util.editor.Clipboard.Type getType() {
        return null;
    }
    
    public final void setType(@org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.util.editor.Clipboard.Type p0) {
    }
    
    public final void update(@org.jetbrains.annotations.NotNull()
    java.io.File file, @org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.util.editor.Clipboard.Type t, @org.jetbrains.annotations.NotNull()
    android.view.View view) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Lorg/navgurukul/webide/util/editor/Clipboard$Type;", "", "(Ljava/lang/String;I)V", "COPY", "CUT", "webIDE_debug"})
    public static enum Type {
        /*public static final*/ COPY /* = new COPY() */,
        /*public static final*/ CUT /* = new CUT() */;
        
        Type() {
        }
    }
}