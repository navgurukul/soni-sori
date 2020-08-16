/*
The code below is attributed to
https://github.com/chaquo/chaquopy-console
 */

package org.navgurukul.playground.chaquopy.console;

import android.app.Application;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.navgurukul.playground.R;
import org.navgurukul.playground.chaquopy.utils.PythonConsoleActivity;

import java.util.Objects;


public class ReplActivity extends PythonConsoleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check of Python is started or not or else start
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        super.onCreate(savedInstanceState);
        // VISIBLE_PASSWORD is necessary to prevent some versions of the Google keyboard from
        // displaying the suggestion bar.
        ((TextView) findViewById(resId("id", "etInput"))).setInputType(
                InputType.TYPE_CLASS_TEXT +
                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS +
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        setSupportActionBar((Toolbar) findViewById(resId("id", "toolBar")));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected Class<? extends Task> getTaskClass() {
        return Task.class;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // If home button pressed Exit the activity
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Maintain REPL state unless the loop has been terminated, e.g. by typing `exit()`. Requires
    // the activity to be in its own task (see AndroidManifest).
    @Override
    public void onBackPressed() {
        if (task.getState() == Thread.State.RUNNABLE) {
            Toast.makeText(this,
                    getString(R.string.toast_exit_console), Toast.LENGTH_LONG).show();
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    // =============================================================================================

    public static class Task extends PythonConsoleActivity.Task {
        public Task(Application app) {
            super(app);
        }

        @Override
        public void run() {
            py.getModule("chaquopy.demo.repl")
                    .callAttr("AndroidConsole", getApplication().getBaseContext())
                    .callAttr("interact");
        }
    }

}
