package org.navgurukul.webide.ui.activity;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 72\u00020\u0001:\u00017B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0011H\u0002J\"\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001f2\b\u0010!\u001a\u0004\u0018\u00010\"H\u0014J\b\u0010#\u001a\u00020\u001dH\u0016J\u0010\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020&H\u0016J\u0012\u0010\'\u001a\u00020\u001d2\b\u0010(\u001a\u0004\u0018\u00010)H\u0014J\u0010\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-H\u0016J\u0010\u0010.\u001a\u00020+2\u0006\u0010/\u001a\u000200H\u0016J\u0012\u00101\u001a\u00020\u001d2\b\u0010(\u001a\u0004\u0018\u00010)H\u0014J\u0010\u00102\u001a\u00020+2\u0006\u0010,\u001a\u00020-H\u0016J\u0018\u00103\u001a\u00020\u001d2\u0006\u0010\u001b\u001a\u00020\u00112\u0006\u00104\u001a\u00020+H\u0002J\b\u00105\u001a\u00020+H\u0016J\b\u00106\u001a\u00020\u001dH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u0014\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u0015X\u0082.\u00a2\u0006\u0004\n\u0002\u0010\u0016R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u00068"}, d2 = {"Lorg/navgurukul/webide/ui/activity/ProjectActivity;", "Lorg/navgurukul/commonui/platform/BaseActivity;", "()V", "adapter", "Lorg/navgurukul/webide/ui/adapter/FileBrowserAdapter;", "binding", "Lorg/navgurukul/webide/databinding/ActivityProjectBinding;", "fileAdapter", "Lorg/navgurukul/webide/ui/adapter/FileAdapter;", "fileSpinner", "Landroid/widget/Spinner;", "indexFile", "Ljava/io/File;", "prefs", "Landroid/content/SharedPreferences;", "projectDir", "projectName", "", "projectViewModel", "Lorg/navgurukul/webide/ui/viewmodel/ProjectViewModel;", "props", "", "[Ljava/lang/String;", "toggle", "Landroidx/appcompat/app/ActionBarDrawerToggle;", "getFragment", "Landroidx/fragment/app/Fragment;", "filePath", "onActivityResult", "", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onBackPressed", "onConfigurationChanged", "newConfig", "Landroid/content/res/Configuration;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateOptionsMenu", "", "menu", "Landroid/view/Menu;", "onOptionsItemSelected", "item", "Landroid/view/MenuItem;", "onPostCreate", "onPrepareOptionsMenu", "setFragment", "add", "shouldInstallDynamicModule", "showAbout", "Companion", "webIDE_debug"})
public final class ProjectActivity extends org.navgurukul.commonui.platform.BaseActivity {
    private org.navgurukul.webide.ui.viewmodel.ProjectViewModel projectViewModel;
    private android.widget.Spinner fileSpinner;
    private org.navgurukul.webide.ui.adapter.FileAdapter fileAdapter;
    private androidx.appcompat.app.ActionBarDrawerToggle toggle;
    private java.lang.String projectName;
    private java.io.File projectDir;
    private java.io.File indexFile;
    private java.lang.String[] props;
    private android.content.SharedPreferences prefs;
    private org.navgurukul.webide.ui.adapter.FileBrowserAdapter adapter;
    private org.navgurukul.webide.databinding.ActivityProjectBinding binding;
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.ui.activity.ProjectActivity.Companion Companion = null;
    private static final int IMPORT_FILE = 101;
    private java.util.HashMap _$_findViewCache;
    
    public ProjectActivity() {
        super();
    }
    
    @java.lang.Override()
    public boolean shouldInstallDynamicModule() {
        return false;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setFragment(java.lang.String filePath, boolean add) {
    }
    
    private final androidx.fragment.app.Fragment getFragment(java.lang.String filePath) {
        return null;
    }
    
    @java.lang.Override()
    public boolean onPrepareOptionsMenu(@org.jetbrains.annotations.NotNull()
    android.view.Menu menu) {
        return false;
    }
    
    @java.lang.Override()
    protected void onPostCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onConfigurationChanged(@org.jetbrains.annotations.NotNull()
    android.content.res.Configuration newConfig) {
    }
    
    @java.lang.Override()
    public void onBackPressed() {
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
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    private final void showAbout() {
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lorg/navgurukul/webide/ui/activity/ProjectActivity$Companion;", "", "()V", "IMPORT_FILE", "", "newIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "projectName", "", "webIDE_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.content.Intent newIntent(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        java.lang.String projectName) {
            return null;
        }
    }
}