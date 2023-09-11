package org.navgurukul.webide.ui.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0010H\u0016R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\fR\u000e\u0010\r\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\f\u00a8\u0006\u0014"}, d2 = {"Lorg/navgurukul/webide/ui/adapter/IntroAdapter;", "Landroidx/fragment/app/FragmentStatePagerAdapter;", "context", "Landroid/content/Context;", "fm", "Landroidx/fragment/app/FragmentManager;", "(Landroid/content/Context;Landroidx/fragment/app/FragmentManager;)V", "bgColors", "", "desc", "", "", "[Ljava/lang/String;", "images", "titles", "getCount", "", "getItem", "Lorg/navgurukul/webide/ui/fragment/IntroFragment;", "position", "webIDE_debug"})
public final class IntroAdapter extends androidx.fragment.app.FragmentStatePagerAdapter {
    private final int[] bgColors = null;
    private final int[] images = {org.navgurukul.webide.R.drawable.ic_intro_logo_n, org.navgurukul.webide.R.drawable.ic_intro_editor, org.navgurukul.webide.R.drawable.ic_intro_git, org.navgurukul.webide.R.drawable.ic_intro_done};
    private final java.lang.String[] titles = null;
    private final java.lang.String[] desc = null;
    
    public IntroAdapter(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    androidx.fragment.app.FragmentManager fm) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public org.navgurukul.webide.ui.fragment.IntroFragment getItem(int position) {
        return null;
    }
    
    @java.lang.Override()
    public int getCount() {
        return 0;
    }
}