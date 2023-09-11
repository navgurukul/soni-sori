package org.navgurukul.webide.util.project;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\n\u0010\u0007\u001a\u0006\u0012\u0002\b\u00030\bJ\u001e\u0010\t\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fJ.\u0010\u000e\u001a\u00020\n2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u000f\u00a8\u0006\u0013"}, d2 = {"Lorg/navgurukul/webide/util/project/DataValidator;", "", "()V", "removeBroken", "", "context", "Landroid/content/Context;", "objectsList", "Ljava/util/ArrayList;", "validateClone", "", "name", "Lcom/google/android/material/textfield/TextInputEditText;", "remote", "validateCreate", "Lcom/google/android/material/textfield/TextInputLayout;", "author", "description", "keywords", "webIDE_debug"})
public final class DataValidator {
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.util.project.DataValidator INSTANCE = null;
    
    private DataValidator() {
        super();
    }
    
    public final boolean validateCreate(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.google.android.material.textfield.TextInputLayout name, @org.jetbrains.annotations.NotNull()
    com.google.android.material.textfield.TextInputLayout author, @org.jetbrains.annotations.NotNull()
    com.google.android.material.textfield.TextInputLayout description, @org.jetbrains.annotations.NotNull()
    com.google.android.material.textfield.TextInputLayout keywords) {
        return false;
    }
    
    public final boolean validateClone(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.google.android.material.textfield.TextInputEditText name, @org.jetbrains.annotations.NotNull()
    com.google.android.material.textfield.TextInputEditText remote) {
        return false;
    }
    
    public final void removeBroken(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.util.ArrayList<?> objectsList) {
    }
}