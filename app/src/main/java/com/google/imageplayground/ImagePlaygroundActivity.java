/* 
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.imageplayground;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.imageplayground.codegen.DexImageScript;

public class ImagePlaygroundActivity extends Activity {

    EditText scriptField;
    String userScript;
    Button startButton;
    TextView resultText;
    EditText argumentField;

    DexImageScript dexScript = null;
    String lastUserScript = "";

    static final String SCRIPT_UNTESTED_PREF = "scriptUntested";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        scriptField = (EditText) findViewById(R.id.scriptText);
        argumentField = (EditText) findViewById(R.id.argumentText);

        SyntaxHighlighter.watchTextField(scriptField);
        startButton = (Button) findViewById(R.id.startButton);
        resultText = (TextView) findViewById(R.id.resultText);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestComputation();
            }
        });

        updateFromPreferences();
    }

    @Override
    public void onPause() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    void updateFromPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        this.userScript = prefs.getString("script", "return (x+y+z)");
        scriptField.setText(this.userScript);
    }

    void saveScript(String script) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putString("script", script);
        editor.commit();
    }

    void updateScriptUntestedPref(boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
        editor.putBoolean(SCRIPT_UNTESTED_PREF, value);
        editor.commit();
    }


    public void requestComputation() {
        DexImageScript script = new DexImageScript();
        String userScript = scriptField.getText().toString();
        if (userScript != null && (!userScript.equals(lastUserScript))) {
            Toast.makeText(this, "function can only be changed once! ",
                    Toast.LENGTH_LONG).show();
            // new script: mark untested and record current time
            updateScriptUntestedPref(true);
            lastUserScript = userScript;
            saveScript(userScript);
            script.createScript(this, userScript);
            android.util.Log.i("ImagePlaygroundActivity", "compute method regenerated!");
        }
        String argumentString = argumentField.getText().toString();
        String[] arguments = argumentString.split(",");
        int result = script.compute(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]));
        resultText.setText("Result is: " + result);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
