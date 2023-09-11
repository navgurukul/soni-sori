package org.navgurukul.webide.ui.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0015B#\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\b\u0010\u000b\u001a\u00020\fH\u0016J\u001c\u0010\r\u001a\u00020\u000e2\n\u0010\u000f\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0010\u001a\u00020\fH\u0016J\u001c\u0010\u0011\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\fH\u0016R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/LogsAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lorg/navgurukul/webide/ui/adapter/LogsAdapter$ViewHolder;", "localWithoutIndex", "", "jsLogs", "", "Landroid/webkit/ConsoleMessage;", "darkTheme", "", "(Ljava/lang/String;Ljava/util/List;Z)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "ViewHolder", "webIDE_debug"})
public final class LogsAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<org.navgurukul.webide.ui.adapter.LogsAdapter.ViewHolder> {
    private final java.lang.String localWithoutIndex = null;
    private final java.util.List<android.webkit.ConsoleMessage> jsLogs = null;
    private final boolean darkTheme = false;
    
    public LogsAdapter(@org.jetbrains.annotations.NotNull()
    java.lang.String localWithoutIndex, @org.jetbrains.annotations.NotNull()
    java.util.List<? extends android.webkit.ConsoleMessage> jsLogs, boolean darkTheme) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public org.navgurukul.webide.ui.adapter.LogsAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    org.navgurukul.webide.ui.adapter.LogsAdapter.ViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/LogsAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Lorg/navgurukul/webide/databinding/ItemLogBinding;", "(Lorg/navgurukul/webide/ui/adapter/LogsAdapter;Lorg/navgurukul/webide/databinding/ItemLogBinding;)V", "bind", "", "consoleMessage", "Landroid/webkit/ConsoleMessage;", "getLogColor", "", "messageLevel", "Landroid/webkit/ConsoleMessage$MessageLevel;", "webIDE_debug"})
    public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private org.navgurukul.webide.databinding.ItemLogBinding v;
        
        public ViewHolder(@org.jetbrains.annotations.NotNull()
        org.navgurukul.webide.databinding.ItemLogBinding v) {
            super(null);
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        android.webkit.ConsoleMessage consoleMessage) {
        }
        
        @androidx.annotation.ColorInt()
        private final int getLogColor(android.webkit.ConsoleMessage.MessageLevel messageLevel) {
            return 0;
        }
    }
}