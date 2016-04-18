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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Date;

import org.json.JSONObject;

import com.google.imageplayground.util.JavaUtils;

public class ScriptFile {
    
    static String PROPERTIES_FILENAME = "properties.json";
    static String SCRIPT_NAME_ATTRIBUTE = "scriptName";
    static String DATE_CREATED_ATTRIBUTE = "dateCreated";
    static String DATE_MODIFIED_ATTRIBUTE = "dateModified";
    
    String directoryPath;
    String scriptName;
    Date dateCreated;
    Date dateModified;
    String scriptContent;
    
    public static ScriptFile loadFromDirectory(File dir) {
        try {
            String propFilename = dir.getPath() + File.separator + PROPERTIES_FILENAME;
            byte[] propertiesFileData = JavaUtils.bytesForInputStream(new FileInputStream(propFilename));
            JSONObject obj = new JSONObject(new String(propertiesFileData, "utf-8"));
            
            long createdTS = obj.optLong(DATE_CREATED_ATTRIBUTE);
            long modifiedTS = obj.optLong(DATE_MODIFIED_ATTRIBUTE);
            Date createdDate = (createdTS==0) ? null : new Date(createdTS);
            Date modifiedDate = (modifiedTS==0) ? null : new Date(modifiedTS);
            return new ScriptFile(dir.getPath(), obj.getString(SCRIPT_NAME_ATTRIBUTE), createdDate, modifiedDate);
        }
        catch(Exception ex) {
            return null;
        }
    }

    public static ScriptFile createNewScriptFile(String directoryPath, String scriptName, String scriptContent) {
        (new File(directoryPath)).mkdirs();
        Date now = new Date();
        JSONObject json = new JSONObject();
        try {
            json.put(SCRIPT_NAME_ATTRIBUTE, scriptName);
            json.put(DATE_CREATED_ATTRIBUTE, now.getTime());
            json.put(DATE_MODIFIED_ATTRIBUTE, now.getTime());
            
            FileWriter fw = new FileWriter(directoryPath + File.separator + PROPERTIES_FILENAME);
            fw.write(json.toString(2));
            fw.close();            
        }
        catch(Exception ex) {
            return null;
        }
        
        ScriptFile sf = new ScriptFile(directoryPath, scriptName, now, now);
        if (sf.saveScriptContent(scriptContent)) {
            return sf;
        }
        return null;
    }
    
    protected ScriptFile(String directoryPath, String scriptName, Date dateCreated, Date dateModified) {
        this.directoryPath = directoryPath;
        this.scriptName = scriptName;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }
    
    public String getScriptName() {
        return scriptName;
    }
    
    public String getDirectoryPath() {
        return directoryPath;
    }
    
    String scriptPath() {
        String dirname = this.directoryPath.substring(this.directoryPath.lastIndexOf(File.separator)+1);
        return this.directoryPath + File.separator + dirname + ".txt";
    }
    
    public String getScriptContent() {
        if (scriptContent==null) {
            try {
                return new String(JavaUtils.bytesForInputStream(new FileInputStream(scriptPath())), "utf-8");
            }
            catch(Exception ex) {
                return "";
            }
        }
        return scriptContent;
    }
    
    public boolean saveScriptContent(String updatedContent) {
        try {
            String path = scriptPath();
            String newScriptPath = path + ".new";
            FileWriter out = new FileWriter(newScriptPath);
            out.write(updatedContent);
            out.close();
            return (new File(newScriptPath)).renameTo(new File(path));
        }
        catch(Exception ex) {
            return false;
        }
    }
    
    public boolean deleteScript() {
        return (new File(this.directoryPath)).delete();
    }
    
}
