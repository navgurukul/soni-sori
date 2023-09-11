package org.navgurukul.webide.ui.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u001aB\u001d\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\fJ\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u001c\u0010\u0013\u001a\u00020\u000e2\n\u0010\u0014\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0015\u001a\u00020\u0012H\u0016J\u001c\u0010\u0016\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0012H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/RemotesAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lorg/navgurukul/webide/ui/adapter/RemotesAdapter$RemotesHolder;", "mainContext", "Landroid/content/Context;", "remotesView", "Landroid/view/View;", "repo", "Ljava/io/File;", "(Landroid/content/Context;Landroid/view/View;Ljava/io/File;)V", "remotesList", "Ljava/util/ArrayList;", "", "add", "", "remote", "url", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "RemotesHolder", "webIDE_debug"})
public final class RemotesAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<org.navgurukul.webide.ui.adapter.RemotesAdapter.RemotesHolder> {
    private final android.content.Context mainContext = null;
    private final android.view.View remotesView = null;
    private final java.io.File repo = null;
    private final java.util.ArrayList<java.lang.String> remotesList = null;
    
    public RemotesAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context mainContext, @org.jetbrains.annotations.NotNull()
    android.view.View remotesView, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public org.navgurukul.webide.ui.adapter.RemotesAdapter.RemotesHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.adapter.RemotesAdapter.RemotesHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void add(@org.jetbrains.annotations.NotNull()
    java.lang.String remote, @org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\r"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/RemotesAdapter$RemotesHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "rootView", "Lorg/navgurukul/webide/databinding/ItemRemoteBinding;", "(Lorg/navgurukul/webide/ui/adapter/RemotesAdapter;Lorg/navgurukul/webide/databinding/ItemRemoteBinding;)V", "getRootView", "()Lorg/navgurukul/webide/databinding/ItemRemoteBinding;", "setRootView", "(Lorg/navgurukul/webide/databinding/ItemRemoteBinding;)V", "bind", "", "remote", "", "webIDE_debug"})
    public final class RemotesHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private org.navgurukul.webide.databinding.ItemRemoteBinding rootView;
        
        public RemotesHolder(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemRemoteBinding rootView) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final org.navgurukul.webide.databinding.ItemRemoteBinding getRootView() {
            return null;
        }
        
        public final void setRootView(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemRemoteBinding p0) {
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        java.lang.String remote) {
        }
    }
}