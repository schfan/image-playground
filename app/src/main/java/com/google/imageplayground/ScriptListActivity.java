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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.imageplayground.scripts.ScriptFile;
import com.google.imageplayground.scripts.ScriptList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ScriptListActivity extends Activity {
    
    public static Intent intentWithScriptDirectory(Context parent, String directory) {
        Intent intent = new Intent(parent, ScriptListActivity.class);
        intent.putExtra("scriptDirectory", directory);
        return intent;
    }
    
    ListView scriptListView;
    EditText scriptTextView;
    
    String scriptDirectoryPath;
    ScriptList scriptList;
    List scriptFileMaps;
    ScriptFile selectedScript;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.script_list);
        
        scriptTextView = (EditText)findViewById(R.id.scriptTextView);
        // disable editing, not ideal because this also disables scrolling
        scriptTextView.setFocusable(false);
        scriptTextView.setEnabled(false);
        SyntaxHighlighter.watchTextField(scriptTextView);
        
        scriptListView = (ListView)findViewById(R.id.scriptListView);
        scriptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleScriptListSelection(position);
            }
        });
        
        this.scriptDirectoryPath = this.getIntent().getStringExtra("scriptDirectory");
        scriptList = new ScriptList(this.scriptDirectoryPath);
        updateScriptList();
    }
    
    void updateScriptList() {
        scriptList = new ScriptList(this.scriptDirectoryPath);
        scriptFileMaps = new ArrayList();
        
        for(ScriptFile sf : scriptList.getScriptFiles()) {
            Map dmap = new HashMap();
            dmap.put("title", sf.getScriptName());
            dmap.put("scriptFile", sf);
            scriptFileMaps.add(dmap);
        }
        
        ListAdapter adapter = new SimpleAdapter(this, scriptFileMaps, 
                R.layout.script_list_row, 
                new String[] {"title"}, 
                new int[] {R.id.listrow_text1});
        
        scriptListView.setAdapter(adapter);
    }
    
    void handleScriptListSelection(int position) {
        selectedScript = (ScriptFile)((Map)scriptFileMaps.get(position)).get("scriptFile");
        scriptTextView.setText(selectedScript.getScriptContent());
    }
    
    public void onClick_loadScript(View view) {
        Intent intent = new Intent();
        intent.setData(Uri.fromFile(new File(selectedScript.getDirectoryPath())));
        
        this.setResult(Activity.RESULT_OK, intent);
        this.finish();
    }
    
    public void onClick_deleteScript(View view) {
        
    }
}
