package org.navgurukul.webide.util.project;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005H\u0002J \u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0016\u0010\u0010\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005JP\u0010\u0011\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00052\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aJ:\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00052\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002J\u001c\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010 \u001a\u00020\u0005J\u0016\u0010!\u001a\u00020\"2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0005J\u0012\u0010#\u001a\u0004\u0018\u00010\u001f2\u0006\u0010$\u001a\u00020\u001fH\u0002J\u0018\u0010%\u001a\u0004\u0018\u00010\u001f2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010 \u001a\u00020\u0005J\u001e\u0010&\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\'\u001a\u00020\u001f2\u0006\u0010(\u001a\u00020\u0005J\u000e\u0010)\u001a\u00020\u00052\u0006\u0010*\u001a\u00020+J&\u0010,\u001a\u00020\u001c2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020\u0005JF\u00100\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u00101\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018J\u000e\u00102\u001a\u00020\u001c2\u0006\u00103\u001a\u00020\u001fJ\u000e\u00104\u001a\u00020\u001c2\u0006\u00103\u001a\u00020\u001fJ\u0016\u00105\u001a\u00020\u001c2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u00106\u001a\u00020\u0005R\u0019\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007\u00a8\u00067"}, d2 = {"Lorg/navgurukul/webide/util/project/ProjectManager;", "", "()V", "TYPES", "", "", "getTYPES", "()[Ljava/lang/String;", "[Ljava/lang/String;", "copyIcon", "", "context", "Landroid/content/Context;", "name", "stream", "Ljava/io/InputStream;", "deleteProject", "generate", "author", "description", "keywords", "adapter", "Lorg/navgurukul/webide/ui/adapter/ProjectAdapter;", "view", "Landroid/view/View;", "type", "", "generateDefault", "", "getAllFile", "Lkotlin/sequences/Sequence;", "Ljava/io/File;", "project", "getFavicon", "Landroid/graphics/Bitmap;", "getFaviconFile", "dir", "getIndexFile", "getRelativePath", "file", "projectName", "humanReadableByteCount", "bytes", "", "importFile", "fileUri", "Landroid/net/Uri;", "fileName", "importProject", "fileStr", "isBinaryFile", "f", "isImageFile", "isValid", "string", "webIDE_debug"})
public final class ProjectManager {
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.util.project.ProjectManager INSTANCE = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String[] TYPES = {"Default"};
    
    private ProjectManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getTYPES() {
        return null;
    }
    
    public final void generate(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String author, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.lang.String keywords, @org.jetbrains.annotations.Nullable()
    java.io.InputStream stream, @org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.adapter.ProjectAdapter adapter, @org.jetbrains.annotations.NotNull()
    android.view.View view, int type) {
    }
    
    private final boolean generateDefault(android.content.Context context, java.lang.String name, java.lang.String author, java.lang.String description, java.lang.String keywords, java.io.InputStream stream) {
        return false;
    }
    
    public final void importProject(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String fileStr, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    java.lang.String author, @org.jetbrains.annotations.NotNull()
    java.lang.String description, @org.jetbrains.annotations.NotNull()
    java.lang.String keywords, @org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.adapter.ProjectAdapter adapter, @org.jetbrains.annotations.NotNull()
    android.view.View view) {
    }
    
    public final boolean isValid(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String string) {
        return false;
    }
    
    public final void deleteProject(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    private final java.io.File getFaviconFile(java.io.File dir) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.io.File getIndexFile(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String project) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlin.sequences.Sequence<java.io.File> getAllFile(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String project) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRelativePath(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.io.File file, @org.jetbrains.annotations.NotNull()
    java.lang.String projectName) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.graphics.Bitmap getFavicon(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String name) {
        return null;
    }
    
    private final void copyIcon(android.content.Context context, java.lang.String name) {
    }
    
    private final void copyIcon(android.content.Context context, java.lang.String name, java.io.InputStream stream) {
    }
    
    public final boolean isBinaryFile(@org.jetbrains.annotations.NotNull()
    java.io.File f) {
        return false;
    }
    
    public final boolean isImageFile(@org.jetbrains.annotations.NotNull()
    java.io.File f) {
        return false;
    }
    
    public final boolean importFile(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    android.net.Uri fileUri, @org.jetbrains.annotations.NotNull()
    java.lang.String fileName) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String humanReadableByteCount(long bytes) {
        return null;
    }
}