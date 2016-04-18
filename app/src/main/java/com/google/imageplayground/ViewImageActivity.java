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

import com.google.imageplayground.util.AndroidUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

/** Activity for displaying a single image. The user can delete the image, or share it in
 * its PNG or HTML representations.
 */
public class ViewImageActivity extends Activity {
    
    public static final int DELETE_RESULT = Activity.RESULT_FIRST_USER;
    
    ImageView imageView;
    Uri imageUri;

    public static Intent startActivityWithImageURI(Activity parent, Uri imageURI, String type) {
        Intent intent = new Intent(parent, ViewImageActivity.class);
        intent.setDataAndType(imageURI, type);
        parent.startActivityForResult(intent, 0);
        return intent;
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.imageview);
        
        imageView = (ImageView)findViewById(R.id.imageView);
        imageUri = getIntent().getData();
        
        // assume full screen, there's no good way to get notified once layout happens and views have nonzero width/height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        try {
            imageView.setImageBitmap(AndroidUtils.scaledBitmapFromURIWithMinimumSize(this, imageUri, 
                    dm.widthPixels, dm.heightPixels));
        }
        catch(Exception ex) {}
    }
    
    // touch handler methods called via onClick bindings in imageview.xml
    public void goBack(View view) {
        this.finish();
    }
    
    public void deleteImage(View view) {
        String path = this.getIntent().getData().getPath();
        (new File(path)).delete();
        this.setResult(DELETE_RESULT);
        this.finish();
    }
    
    public void shareImage(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(this.getIntent().getType());
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Picture Using:"));
    }
    
}
