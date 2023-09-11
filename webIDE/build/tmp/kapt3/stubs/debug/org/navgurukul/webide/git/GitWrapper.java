package org.navgurukul.webide.git;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0018\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ&\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0016\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000bH\u0002J&\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0015\u001a\u00020\u000bJ>\u0010\u0016\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bJ&\u0010\u001c\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u000bJ.\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020\u000b2\u0006\u0010 \u001a\u00020\u000eJ/\u0010!\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0012\u0010\"\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0#\"\u00020\u000b\u00a2\u0006\u0002\u0010$J(\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020(J6\u0010*\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bJ\u001e\u0010+\u001a\n\u0012\u0004\u0012\u00020-\u0018\u00010,2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u001e\u0010.\u001a\n\u0012\u0004\u0012\u00020/\u0018\u00010,2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u001a\u00100\u001a\u0004\u0018\u0001012\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0018\u00102\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u001f\u00103\u001a\u0004\u0018\u0001042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0000\u00a2\u0006\u0002\b5J\u001e\u00106\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bJ\u001e\u00107\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u0001082\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u001e\u00109\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0006J6\u0010:\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bJ>\u0010;\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u000b2\u0006\u0010<\u001a\u00020=2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bJ\u001e\u0010>\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bJ/\u0010?\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0012\u0010@\u001a\n\u0012\u0006\b\u0001\u0012\u00020A0#\"\u00020A\u00a2\u0006\u0002\u0010B\u00a8\u0006C"}, d2 = {"Lorg/navgurukul/webide/git/GitWrapper;", "", "()V", "add", "", "view", "Landroid/view/View;", "repo", "Ljava/io/File;", "addRemote", "remote", "", "url", "canCheckout", "", "canCommit", "changeTextToNone", "text", "checkout", "context", "Landroid/content/Context;", "branch", "clone", "adapter", "Lorg/navgurukul/webide/ui/adapter/ProjectAdapter;", "remoteUrl", "username", "password", "commit", "message", "createBranch", "branchName", "checked", "deleteBranch", "branches", "", "(Landroid/view/View;Ljava/io/File;[Ljava/lang/String;)V", "diff", "Landroid/text/SpannableString;", "hash1", "Lorg/eclipse/jgit/lib/ObjectId;", "hash2", "fetch", "getBranches", "", "Lorg/eclipse/jgit/lib/Ref;", "getCommits", "Lorg/eclipse/jgit/revwalk/RevCommit;", "getConfig", "Lorg/eclipse/jgit/lib/StoredConfig;", "getCurrentBranch", "getGit", "Lorg/eclipse/jgit/api/Git;", "getGit$webIDE_debug", "getRemoteUrl", "getRemotes", "Ljava/util/ArrayList;", "init", "pull", "push", "options", "", "removeRemote", "status", "t", "Landroid/widget/TextView;", "(Landroid/view/View;Ljava/io/File;[Landroid/widget/TextView;)V", "webIDE_debug"})
public final class GitWrapper {
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.git.GitWrapper INSTANCE = null;
    
    private GitWrapper() {
        super();
    }
    
    public final void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    android.view.View view) {
    }
    
    public final void add(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
    }
    
    public final void commit(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String message) {
    }
    
    private final java.lang.String changeTextToNone(java.lang.String text) {
        return null;
    }
    
    public final void status(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    android.widget.TextView... t) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<org.eclipse.jgit.revwalk.RevCommit> getCommits(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<org.eclipse.jgit.lib.Ref> getBranches(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        return null;
    }
    
    public final void createBranch(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String branchName, boolean checked) {
    }
    
    public final void deleteBranch(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String... branches) {
    }
    
    public final void checkout(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String branch) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCurrentBranch(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        return null;
    }
    
    public final void clone(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.adapter.ProjectAdapter adapter, @org.jetbrains.annotations.NotNull()
    java.lang.String remoteUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
    }
    
    public final void push(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String remoteUrl, @org.jetbrains.annotations.NotNull()
    boolean[] options, @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
    }
    
    public final void pull(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String remote, @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
    }
    
    public final void fetch(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String remote, @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final org.eclipse.jgit.api.Git getGit$webIDE_debug(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        return null;
    }
    
    private final org.eclipse.jgit.lib.StoredConfig getConfig(android.view.View view, java.io.File repo) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRemoteUrl(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String remote) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.ArrayList<java.lang.String> getRemotes(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        return null;
    }
    
    public final void addRemote(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String remote, @org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
    
    public final void removeRemote(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String remote) {
    }
    
    public final boolean canCommit(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        return false;
    }
    
    public final boolean canCheckout(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo) {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.text.SpannableString diff(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    org.eclipse.jgit.lib.ObjectId hash1, @org.jetbrains.annotations.NotNull()
    org.eclipse.jgit.lib.ObjectId hash2) {
        return null;
    }
}