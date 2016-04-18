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

package com.google.imageplayground.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptList {
    
    String baseDirectory;
    List<ScriptFile> scriptFiles;
    
    public ScriptList(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
    
    public List<ScriptFile> getScriptFiles() {
        if (this.scriptFiles==null) {
            this.scriptFiles = new ArrayList<ScriptFile>();
            File dir = new File(baseDirectory);
            if (dir.isDirectory()) {
                for(File f : dir.listFiles()) {
                    if (f.isDirectory()) {
                        ScriptFile sf = ScriptFile.loadFromDirectory(f);
                        if (sf!=null) {
                            this.scriptFiles.add(sf);
                        }
                    }
                }
            }
        }
        return this.scriptFiles;
    }
    
    public ScriptFile createNewScriptFile(String scriptName, String scriptContent) {
        // find a directory name that doesn't conflict
        String dirname = baseDirectory + File.separator + findUniqueDirectoryName(scriptName);
        ScriptFile scriptFile = ScriptFile.createNewScriptFile(dirname, scriptName, scriptContent);
        if (scriptFile!=null && this.scriptFiles!=null) {
            this.scriptFiles.add(scriptFile);
        }
        return scriptFile;
    }
    
    boolean hasDirectoryNamed(String scriptName) {
        return (new File(baseDirectory + File.separator + scriptName)).exists();
    }
    
    String findUniqueDirectoryName(String name) {
        String dirname = sanitizeFilename(name);
        if (hasDirectoryNamed(dirname)) {
            int counter = 1;
            while (true) {
                dirname = name + "_" + counter;
                if (!hasDirectoryNamed(dirname)) break;
                ++counter;
            }
        }
        return dirname;
    }
    
    static String sanitizeFilename(String filename) {
        filename = filename.replaceAll("/", "-");
        return filename;
    }

}
