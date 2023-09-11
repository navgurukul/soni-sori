// Generated by view binder compiler. Do not edit!
package org.navgurukul.webide.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.navgurukul.webide.R;

public final class DialogRemoteAddBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextInputEditText remoteAddName;

  @NonNull
  public final TextInputEditText remoteAddUrl;

  private DialogRemoteAddBinding(@NonNull LinearLayout rootView,
      @NonNull TextInputEditText remoteAddName, @NonNull TextInputEditText remoteAddUrl) {
    this.rootView = rootView;
    this.remoteAddName = remoteAddName;
    this.remoteAddUrl = remoteAddUrl;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogRemoteAddBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogRemoteAddBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_remote_add, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogRemoteAddBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.remoteAddName;
      TextInputEditText remoteAddName = ViewBindings.findChildViewById(rootView, id);
      if (remoteAddName == null) {
        break missingId;
      }

      id = R.id.remoteAddUrl;
      TextInputEditText remoteAddUrl = ViewBindings.findChildViewById(rootView, id);
      if (remoteAddUrl == null) {
        break missingId;
      }

      return new DialogRemoteAddBinding((LinearLayout) rootView, remoteAddName, remoteAddUrl);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
