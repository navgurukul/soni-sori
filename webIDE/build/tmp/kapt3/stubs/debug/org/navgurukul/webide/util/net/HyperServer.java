package org.navgurukul.webide.util.net;

import java.lang.System;

@kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0005H\u0002J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lorg/navgurukul/webide/util/net/HyperServer;", "Lfi/iki/elonen/NanoHTTPD;", "context", "Landroid/content/Context;", "project", "", "(Landroid/content/Context;Ljava/lang/String;)V", "getMimeType", "uri", "serve", "Lfi/iki/elonen/NanoHTTPD$Response;", "session", "Lfi/iki/elonen/NanoHTTPD$IHTTPSession;", "Companion", "webIDE_debug"})
public final class HyperServer extends fi.iki.elonen.NanoHTTPD {
    private final android.content.Context context = null;
    private final java.lang.String project = null;
    @org.jetbrains.annotations.NotNull()
    public static final org.navgurukul.webide.util.net.HyperServer.Companion Companion = null;
    private static final java.util.Map<java.lang.String, java.lang.String> MIME_TYPES = null;
    public static final int PORT_NUMBER = 8080;
    
    public HyperServer(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String project) {
        super(0);
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public fi.iki.elonen.NanoHTTPD.Response serve(@org.jetbrains.annotations.NotNull()
    fi.iki.elonen.NanoHTTPD.IHTTPSession session) {
        return null;
    }
    
    private final java.lang.String getMimeType(java.lang.String uri) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 5, 1}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lorg/navgurukul/webide/util/net/HyperServer$Companion;", "", "()V", "MIME_TYPES", "", "", "PORT_NUMBER", "", "webIDE_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}