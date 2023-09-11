// Generated by view binder compiler. Do not edit!
package org.navgurukul.webide.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.navgurukul.webide.R;

public final class DialogCreate2Binding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final TextInputLayout authorLayout;

  @NonNull
  public final RadioButton chooseIcon;

  @NonNull
  public final RadioButton defaultIcon;

  @NonNull
  public final TextInputLayout descLayout;

  @NonNull
  public final ImageView faviconImage;

  @NonNull
  public final TextInputLayout keyLayout;

  @NonNull
  public final TextInputLayout nameLayout;

  @NonNull
  public final Spinner typeSpinner;

  private DialogCreate2Binding(@NonNull CoordinatorLayout rootView,
      @NonNull TextInputLayout authorLayout, @NonNull RadioButton chooseIcon,
      @NonNull RadioButton defaultIcon, @NonNull TextInputLayout descLayout,
      @NonNull ImageView faviconImage, @NonNull TextInputLayout keyLayout,
      @NonNull TextInputLayout nameLayout, @NonNull Spinner typeSpinner) {
    this.rootView = rootView;
    this.authorLayout = authorLayout;
    this.chooseIcon = chooseIcon;
    this.defaultIcon = defaultIcon;
    this.descLayout = descLayout;
    this.faviconImage = faviconImage;
    this.keyLayout = keyLayout;
    this.nameLayout = nameLayout;
    this.typeSpinner = typeSpinner;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogCreate2Binding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogCreate2Binding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_create_2, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogCreate2Binding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.authorLayout;
      TextInputLayout authorLayout = ViewBindings.findChildViewById(rootView, id);
      if (authorLayout == null) {
        break missingId;
      }

      id = R.id.chooseIcon;
      RadioButton chooseIcon = ViewBindings.findChildViewById(rootView, id);
      if (chooseIcon == null) {
        break missingId;
      }

      id = R.id.defaultIcon;
      RadioButton defaultIcon = ViewBindings.findChildViewById(rootView, id);
      if (defaultIcon == null) {
        break missingId;
      }

      id = R.id.descLayout;
      TextInputLayout descLayout = ViewBindings.findChildViewById(rootView, id);
      if (descLayout == null) {
        break missingId;
      }

      id = R.id.faviconImage;
      ImageView faviconImage = ViewBindings.findChildViewById(rootView, id);
      if (faviconImage == null) {
        break missingId;
      }

      id = R.id.keyLayout;
      TextInputLayout keyLayout = ViewBindings.findChildViewById(rootView, id);
      if (keyLayout == null) {
        break missingId;
      }

      id = R.id.nameLayout;
      TextInputLayout nameLayout = ViewBindings.findChildViewById(rootView, id);
      if (nameLayout == null) {
        break missingId;
      }

      id = R.id.typeSpinner;
      Spinner typeSpinner = ViewBindings.findChildViewById(rootView, id);
      if (typeSpinner == null) {
        break missingId;
      }

      return new DialogCreate2Binding((CoordinatorLayout) rootView, authorLayout, chooseIcon,
          defaultIcon, descLayout, faviconImage, keyLayout, nameLayout, typeSpinner);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
