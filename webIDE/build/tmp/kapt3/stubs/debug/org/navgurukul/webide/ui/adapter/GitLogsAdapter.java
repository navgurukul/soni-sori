package org.navgurukul.webide.ui.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0011B\u0015\u0012\u000e\u0010\u0003\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0016J\u001c\u0010\t\u001a\u00020\n2\n\u0010\u000b\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\f\u001a\u00020\bH\u0016J\u001c\u0010\r\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bH\u0016R\u0016\u0010\u0003\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/GitLogsAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lorg/navgurukul/webide/ui/adapter/GitLogsAdapter$ViewHolder;", "gitLogs", "", "Lorg/eclipse/jgit/revwalk/RevCommit;", "(Ljava/util/List;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "ViewHolder", "webIDE_debug"})
public final class GitLogsAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<org.navgurukul.webide.ui.adapter.GitLogsAdapter.ViewHolder> {
    private final java.util.List<org.eclipse.jgit.revwalk.RevCommit> gitLogs = null;
    
    public GitLogsAdapter(@org.jetbrains.annotations.Nullable()
    java.util.List<? extends org.eclipse.jgit.revwalk.RevCommit> gitLogs) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public org.navgurukul.webide.ui.adapter.GitLogsAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.adapter.GitLogsAdapter.ViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b\u00a8\u0006\r"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/GitLogsAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Lorg/navgurukul/webide/databinding/ItemGitLogBinding;", "(Lorg/navgurukul/webide/ui/adapter/GitLogsAdapter;Lorg/navgurukul/webide/databinding/ItemGitLogBinding;)V", "getView", "()Lorg/navgurukul/webide/databinding/ItemGitLogBinding;", "setView", "(Lorg/navgurukul/webide/databinding/ItemGitLogBinding;)V", "bind", "", "commit", "Lorg/eclipse/jgit/revwalk/RevCommit;", "webIDE_debug"})
    public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private org.navgurukul.webide.databinding.ItemGitLogBinding view;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemGitLogBinding view) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final org.navgurukul.webide.databinding.ItemGitLogBinding getView() {
            return null;
        }
        
        public final void setView(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemGitLogBinding p0) {
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        org.eclipse.jgit.revwalk.RevCommit commit) {
        }
    }
}