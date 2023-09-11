package org.navgurukul.webide.git;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0007\b&\u0018\u00002\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001B7\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\u0006\u0010\t\u001a\u00020\n\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\f\u00a2\u0006\u0002\u0010\rJ\u0017\u0010/\u001a\u0002002\b\u00101\u001a\u0004\u0018\u00010\u0003H\u0014\u00a2\u0006\u0002\u00102J\b\u00103\u001a\u000200H\u0014J!\u00104\u001a\u0002002\u0012\u00105\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\f\"\u00020\u0002H\u0014\u00a2\u0006\u0002\u00106R\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R \u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0018\u001a\u00020\u0019X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u001a\u0010\u001e\u001a\u00020\u001fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\fX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010$R\u0011\u0010%\u001a\u00020&\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b)\u0010*\"\u0004\b+\u0010,R \u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b-\u0010\u0015\"\u0004\b.\u0010\u0017\u00a8\u00067"}, d2 = {"Lorg/navgurukul/webide/git/GitTask;", "Landroid/os/AsyncTask;", "", "", "context", "Ljava/lang/ref/WeakReference;", "Landroid/content/Context;", "rootView", "Landroid/view/View;", "repo", "Ljava/io/File;", "messages", "", "(Ljava/lang/ref/WeakReference;Ljava/lang/ref/WeakReference;Ljava/io/File;[Ljava/lang/String;)V", "builder", "Landroidx/core/app/NotificationCompat$Builder;", "getBuilder", "()Landroidx/core/app/NotificationCompat$Builder;", "setBuilder", "(Landroidx/core/app/NotificationCompat$Builder;)V", "getContext", "()Ljava/lang/ref/WeakReference;", "setContext", "(Ljava/lang/ref/WeakReference;)V", "id", "", "getId", "()I", "setId", "(I)V", "manager", "Landroid/app/NotificationManager;", "getManager", "()Landroid/app/NotificationManager;", "setManager", "(Landroid/app/NotificationManager;)V", "[Ljava/lang/String;", "progressMonitor", "Lorg/eclipse/jgit/lib/BatchingProgressMonitor;", "getProgressMonitor", "()Lorg/eclipse/jgit/lib/BatchingProgressMonitor;", "getRepo", "()Ljava/io/File;", "setRepo", "(Ljava/io/File;)V", "getRootView", "setRootView", "onPostExecute", "", "aBoolean", "(Ljava/lang/Boolean;)V", "onPreExecute", "onProgressUpdate", "values", "([Ljava/lang/String;)V", "webIDE_debug"})
public abstract class GitTask extends android.os.AsyncTask<java.lang.String, java.lang.String, java.lang.Boolean> {
    @org.jetbrains.annotations.NotNull()
    private java.lang.ref.WeakReference<android.content.Context> context;
    @org.jetbrains.annotations.NotNull()
    private java.lang.ref.WeakReference<android.view.View> rootView;
    @org.jetbrains.annotations.NotNull()
    private java.io.File repo;
    private java.lang.String[] messages;
    @org.jetbrains.annotations.NotNull()
    private final org.eclipse.jgit.lib.BatchingProgressMonitor progressMonitor = null;
    @org.jetbrains.annotations.NotNull()
    private android.app.NotificationManager manager;
    @org.jetbrains.annotations.NotNull()
    private androidx.core.app.NotificationCompat.Builder builder;
    private int id = 1;
    
    public GitTask(@org.jetbrains.annotations.NotNull()
    java.lang.ref.WeakReference<android.content.Context> context, @org.jetbrains.annotations.NotNull()
    java.lang.ref.WeakReference<android.view.View> rootView, @org.jetbrains.annotations.NotNull()
    java.io.File repo, @org.jetbrains.annotations.NotNull()
    java.lang.String[] messages) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.ref.WeakReference<android.content.Context> getContext() {
        return null;
    }
    
    public final void setContext(@org.jetbrains.annotations.NotNull()
    java.lang.ref.WeakReference<android.content.Context> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.ref.WeakReference<android.view.View> getRootView() {
        return null;
    }
    
    public final void setRootView(@org.jetbrains.annotations.NotNull()
    java.lang.ref.WeakReference<android.view.View> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File getRepo() {
        return null;
    }
    
    public final void setRepo(@org.jetbrains.annotations.NotNull()
    java.io.File p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final org.eclipse.jgit.lib.BatchingProgressMonitor getProgressMonitor() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.app.NotificationManager getManager() {
        return null;
    }
    
    public final void setManager(@org.jetbrains.annotations.NotNull()
    android.app.NotificationManager p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.core.app.NotificationCompat.Builder getBuilder() {
        return null;
    }
    
    public final void setBuilder(@org.jetbrains.annotations.NotNull()
    androidx.core.app.NotificationCompat.Builder p0) {
    }
    
    public final int getId() {
        return 0;
    }
    
    public final void setId(int p0) {
    }
    
    @java.lang.Override()
    protected void onPreExecute() {
    }
    
    @java.lang.Override()
    protected void onProgressUpdate(@org.jetbrains.annotations.NotNull()
    java.lang.String... values) {
    }
    
    @java.lang.Override()
    protected void onPostExecute(@org.jetbrains.annotations.Nullable()
    java.lang.Boolean aBoolean) {
    }
}