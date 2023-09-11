package org.navgurukul.webide.ui.viewmodel;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0006J\u000e\u0010\u000f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0006R1\u0010\u0003\u001a\u0018\u0012\u0014\u0012\u0012\u0012\u0004\u0012\u00020\u00060\u0005j\b\u0012\u0004\u0012\u00020\u0006`\u00070\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\t\u00a8\u0006\u0010"}, d2 = {"Lorg/navgurukul/webide/ui/viewmodel/ProjectViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "openFiles", "Landroidx/lifecycle/MutableLiveData;", "Ljava/util/ArrayList;", "", "Lkotlin/collections/ArrayList;", "getOpenFiles", "()Landroidx/lifecycle/MutableLiveData;", "openFiles$delegate", "Lkotlin/Lazy;", "addOpenFile", "", "file", "removeOpenFile", "webIDE_debug"})
public final class ProjectViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy openFiles$delegate = null;
    
    public ProjectViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.util.ArrayList<java.lang.String>> getOpenFiles() {
        return null;
    }
    
    public final void addOpenFile(@org.jetbrains.annotations.NotNull()
    java.lang.String file) {
    }
    
    public final void removeOpenFile(@org.jetbrains.annotations.NotNull()
    java.lang.String file) {
    }
}