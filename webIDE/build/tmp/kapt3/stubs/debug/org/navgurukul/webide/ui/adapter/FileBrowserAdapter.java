package org.navgurukul.webide.ui.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 \'2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0003\'()B9\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\f\u00a2\u0006\u0002\u0010\u000fJ\b\u0010\u0014\u001a\u00020\u000eH\u0002J\b\u0010\u0015\u001a\u00020\u000eH\u0002J\u0010\u0010\u0016\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\rH\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0019H\u0016J\u0010\u0010\u001d\u001a\u00020\u00192\u0006\u0010\u001c\u001a\u00020\u0019H\u0016J\u0018\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u00022\u0006\u0010\u001c\u001a\u00020\u0019H\u0016J\u0018\u0010 \u001a\u00020\u00022\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0019H\u0016J\b\u0010$\u001a\u00020\u000eH\u0002J\u0010\u0010%\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\rH\u0002J\u0006\u0010&\u001a\u00020\u000eR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\r0\u0012X\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\u0013R\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/FileBrowserAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "context", "Landroid/content/Context;", "projectName", "", "mainView", "Landroid/view/View;", "projectViewModel", "Lorg/navgurukul/webide/ui/viewmodel/ProjectViewModel;", "listener", "Lkotlin/Function1;", "Ljava/io/File;", "", "(Landroid/content/Context;Ljava/lang/String;Landroid/view/View;Lorg/navgurukul/webide/ui/viewmodel/ProjectViewModel;Lkotlin/jvm/functions/Function1;)V", "currentDir", "fileList", "", "[Ljava/io/File;", "createFile", "createFolder", "deleteFile", "file", "getItemCount", "", "getItemId", "", "position", "getItemViewType", "onBindViewHolder", "holder", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "paste", "renameFile", "updateFiles", "Companion", "RootHolder", "ViewHolder", "webIDE_debug"})
public final class FileBrowserAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {
    private final android.content.Context context = null;
    private final java.lang.String projectName = null;
    private final android.view.View mainView = null;
    private final org.navgurukul.webide.ui.viewmodel.ProjectViewModel projectViewModel = null;
    private final kotlin.jvm.functions.Function1<java.io.File, kotlin.Unit> listener = null;
    private java.io.File currentDir;
    private java.io.File[] fileList;
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.ui.adapter.FileBrowserAdapter.Companion Companion = null;
    private static final int TYPE_UP = 0;
    private static final int TYPE_FILE = 1;
    
    public FileBrowserAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String projectName, @org.jetbrains.annotations.NotNull()
    android.view.View mainView, @org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.viewmodel.ProjectViewModel projectViewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.io.File, kotlin.Unit> listener) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public long getItemId(int position) {
        return 0L;
    }
    
    @java.lang.Override()
    public int getItemViewType(int position) {
        return 0;
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void updateFiles() {
    }
    
    private final void createFile() {
    }
    
    private final void createFolder() {
    }
    
    private final void renameFile(java.io.File file) {
    }
    
    private final void paste() {
    }
    
    private final void deleteFile(java.io.File file) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/FileBrowserAdapter$RootHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "itemView", "Lorg/navgurukul/webide/databinding/ItemFileRootBinding;", "(Lorg/navgurukul/webide/ui/adapter/FileBrowserAdapter;Lorg/navgurukul/webide/databinding/ItemFileRootBinding;)V", "bind", "", "webIDE_debug"})
    public final class RootHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        
        public RootHolder(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemFileRootBinding itemView) {
            super(null);
        }
        
        public final void bind() {
        }
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/FileBrowserAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lorg/navgurukul/webide/databinding/ItemFileBrowserBinding;", "(Lorg/navgurukul/webide/ui/adapter/FileBrowserAdapter;Lorg/navgurukul/webide/databinding/ItemFileBrowserBinding;)V", "bind", "", "file", "Ljava/io/File;", "webIDE_debug"})
    public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final org.navgurukul.webide.databinding.ItemFileBrowserBinding binding = null;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemFileBrowserBinding binding) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        java.io.File file) {
        }
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/FileBrowserAdapter$Companion;", "", "()V", "TYPE_FILE", "", "TYPE_UP", "webIDE_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}