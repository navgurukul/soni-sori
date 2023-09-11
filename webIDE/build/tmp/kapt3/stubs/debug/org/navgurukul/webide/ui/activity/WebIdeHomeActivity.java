package org.navgurukul.webide.ui.activity;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 +2\u00020\u00012\u00020\u00022\u00020\u0003:\u0001+B\u0005\u00a2\u0006\u0002\u0010\u0004J\"\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0016J\b\u0010\u001c\u001a\u00020\u001dH\u0016J\u0012\u0010\u001e\u001a\u00020\u00162\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u0014J\u0010\u0010!\u001a\u00020\u001d2\u0006\u0010\"\u001a\u00020#H\u0016J\u0010\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020&H\u0016J\u0010\u0010\'\u001a\u00020\u001d2\u0006\u0010(\u001a\u00020\tH\u0016J\u0010\u0010)\u001a\u00020\u001d2\u0006\u0010*\u001a\u00020\tH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010\nR\u0016\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lorg/navgurukul/webide/ui/activity/WebIdeHomeActivity;", "Lorg/navgurukul/webide/ui/activity/ThemedActivity;", "Landroidx/appcompat/widget/SearchView$OnQueryTextListener;", "Landroidx/appcompat/widget/SearchView$OnCloseListener;", "()V", "binding", "Lorg/navgurukul/webide/databinding/ActivityWebIdeHomeBinding;", "contents", "", "", "[Ljava/lang/String;", "contentsList", "Ljava/util/ArrayList;", "imageStream", "Ljava/io/InputStream;", "prefs", "Landroid/content/SharedPreferences;", "projectAdapter", "Lorg/navgurukul/webide/ui/adapter/ProjectAdapter;", "projectIcon", "Landroid/widget/ImageView;", "onActivityResult", "", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onClose", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateOptionsMenu", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "onQueryTextChange", "newText", "onQueryTextSubmit", "query", "Companion", "webIDE_debug"})
public final class WebIdeHomeActivity extends org.navgurukul.webide.ui.activity.ThemedActivity implements androidx.appcompat.widget.SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnCloseListener {
    private java.lang.String[] contents;
    private java.util.ArrayList<java.lang.String> contentsList;
    private org.navgurukul.webide.ui.adapter.ProjectAdapter projectAdapter;
    private java.io.InputStream imageStream;
    private android.widget.ImageView projectIcon;
    private android.content.SharedPreferences prefs;
    private org.navgurukul.webide.databinding.ActivityWebIdeHomeBinding binding;
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.ui.activity.WebIdeHomeActivity.Companion Companion = null;
    private static final int SELECT_ICON = 100;
    private static final int SETTINGS_CODE = 101;
    private static final int IMPORT_PROJECT = 102;
    private java.util.HashMap _$_findViewCache;
    
    public WebIdeHomeActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public boolean onCreateOptionsMenu(@org.jetbrains.annotations.NotNull()
    android.view.Menu menu) {
        return false;
    }
    
    @java.lang.Override()
    public boolean onOptionsItemSelected(@org.jetbrains.annotations.NotNull()
    android.view.MenuItem item) {
        return false;
    }
    
    @java.lang.Override()
    public void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    @java.lang.Override()
    public boolean onQueryTextSubmit(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return false;
    }
    
    @java.lang.Override()
    public boolean onQueryTextChange(@org.jetbrains.annotations.NotNull()
    java.lang.String newText) {
        return false;
    }
    
    @java.lang.Override()
    public boolean onClose() {
        return false;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lorg/navgurukul/webide/ui/activity/WebIdeHomeActivity$Companion;", "", "()V", "IMPORT_PROJECT", "", "SELECT_ICON", "SETTINGS_CODE", "webIDE_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}