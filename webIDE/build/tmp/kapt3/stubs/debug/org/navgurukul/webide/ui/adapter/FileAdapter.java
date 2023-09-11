package org.navgurukul.webide.ui.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B%\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0016\u0010\u0005\u001a\u0012\u0012\u0004\u0012\u00020\u00020\u0006j\b\u0012\u0004\u0012\u00020\u0002`\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\t\u001a\u00020\nH\u0016J\"\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\b\u0010\u000e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\nH\u0002J\"\u0010\u0013\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\n2\b\u0010\u000e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u001e\u0010\u0014\u001a\u00020\u00152\u0016\u0010\u0016\u001a\u0012\u0012\u0004\u0012\u00020\u00020\u0006j\b\u0012\u0004\u0012\u00020\u0002`\u0007R\u001e\u0010\u0005\u001a\u0012\u0012\u0004\u0012\u00020\u00020\u0006j\b\u0012\u0004\u0012\u00020\u0002`\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/FileAdapter;", "Landroid/widget/ArrayAdapter;", "", "context", "Landroid/content/Context;", "openFiles", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "(Landroid/content/Context;Ljava/util/ArrayList;)V", "getCount", "", "getDropDownView", "Landroid/view/View;", "position", "convertView", "parent", "Landroid/view/ViewGroup;", "getPageTitle", "", "getView", "update", "", "newFiles", "webIDE_debug"})
public final class FileAdapter extends android.widget.ArrayAdapter<java.lang.String> {
    private java.util.ArrayList<java.lang.String> openFiles;
    
    public FileAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> openFiles) {
        super(null, 0);
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.View getView(int position, @org.jetbrains.annotations.Nullable()
    android.view.View convertView, @org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public android.view.View getDropDownView(int position, @org.jetbrains.annotations.Nullable()
    android.view.View convertView, @org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent) {
        return null;
    }
    
    private final java.lang.CharSequence getPageTitle(int position) {
        return null;
    }
    
    @java.lang.Override()
    public int getCount() {
        return 0;
    }
    
    public final void update(@org.jetbrains.annotations.NotNull()
    java.util.ArrayList<java.lang.String> newFiles) {
    }
}