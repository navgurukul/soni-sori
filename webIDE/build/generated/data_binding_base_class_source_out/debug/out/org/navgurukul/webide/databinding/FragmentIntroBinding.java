// Generated by view binder compiler. Do not edit!
package org.navgurukul.webide.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.navgurukul.webide.R;

public final class FragmentIntroBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView slideDesc;

  @NonNull
  public final ImageView slideImage;

  @NonNull
  public final RelativeLayout slideLayout;

  @NonNull
  public final TextView slideTitle;

  private FragmentIntroBinding(@NonNull RelativeLayout rootView, @NonNull TextView slideDesc,
      @NonNull ImageView slideImage, @NonNull RelativeLayout slideLayout,
      @NonNull TextView slideTitle) {
    this.rootView = rootView;
    this.slideDesc = slideDesc;
    this.slideImage = slideImage;
    this.slideLayout = slideLayout;
    this.slideTitle = slideTitle;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentIntroBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentIntroBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_intro, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentIntroBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.slideDesc;
      TextView slideDesc = ViewBindings.findChildViewById(rootView, id);
      if (slideDesc == null) {
        break missingId;
      }

      id = R.id.slideImage;
      ImageView slideImage = ViewBindings.findChildViewById(rootView, id);
      if (slideImage == null) {
        break missingId;
      }

      RelativeLayout slideLayout = (RelativeLayout) rootView;

      id = R.id.slideTitle;
      TextView slideTitle = ViewBindings.findChildViewById(rootView, id);
      if (slideTitle == null) {
        break missingId;
      }

      return new FragmentIntroBinding((RelativeLayout) rootView, slideDesc, slideImage, slideLayout,
          slideTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
