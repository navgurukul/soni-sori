package org.navgurukul.webide.ui.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u001aB+\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\b\u0010\r\u001a\u00020\u000eH\u0016J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0007J\u001c\u0010\u0012\u001a\u00020\u00102\n\u0010\u0013\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0014\u001a\u00020\u000eH\u0016J\u001c\u0010\u0015\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u000eH\u0016J\u000e\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u000eR\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001b"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/ProjectAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lorg/navgurukul/webide/ui/adapter/ProjectAdapter$ProjectHolder;", "mainContext", "Landroid/content/Context;", "projects", "Ljava/util/ArrayList;", "", "layout", "Landroidx/coordinatorlayout/widget/CoordinatorLayout;", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "(Landroid/content/Context;Ljava/util/ArrayList;Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroidx/recyclerview/widget/RecyclerView;)V", "getItemCount", "", "insert", "", "project", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "remove", "ProjectHolder", "webIDE_debug"})
public final class ProjectAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<org.navgurukul.webide.ui.adapter.ProjectAdapter.ProjectHolder> {
    private final android.content.Context mainContext = null;
    private final java.util.ArrayList<java.lang.String> projects = null;
    private final androidx.coordinatorlayout.widget.CoordinatorLayout layout = null;
    private final androidx.recyclerview.widget.RecyclerView recyclerView = null;
    
    public ProjectAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context mainContext, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> projects, @org.jetbrains.annotations.NotNull()
    androidx.coordinatorlayout.widget.CoordinatorLayout layout, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView recyclerView) {
        super();
    }
    
    public final void insert(@org.jetbrains.annotations.NotNull()
    java.lang.String project) {
    }
    
    public final void remove(int position) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public org.navgurukul.webide.ui.adapter.ProjectAdapter.ProjectHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.adapter.ProjectAdapter.ProjectHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\u000f"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/ProjectAdapter$ProjectHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lorg/navgurukul/webide/databinding/ItemProject2Binding;", "(Lorg/navgurukul/webide/ui/adapter/ProjectAdapter;Lorg/navgurukul/webide/databinding/ItemProject2Binding;)V", "getBinding", "()Lorg/navgurukul/webide/databinding/ItemProject2Binding;", "setBinding", "(Lorg/navgurukul/webide/databinding/ItemProject2Binding;)V", "bind", "", "project", "", "position", "", "webIDE_debug"})
    public final class ProjectHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private org.navgurukul.webide.databinding.ItemProject2Binding binding;
        
        public ProjectHolder(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemProject2Binding binding) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final org.navgurukul.webide.databinding.ItemProject2Binding getBinding() {
            return null;
        }
        
        public final void setBinding(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemProject2Binding p0) {
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        java.lang.String project, int position) {
        }
    }
}