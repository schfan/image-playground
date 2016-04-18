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

package com.google.imageplayground.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;

/**
 * Wrapper for the FaceDetector API which simplifies its use.
 */
public class FaceFinder {
    
    static final int DEFAULT_MAX_FACES = 10;
    static final int DEFAULT_FACE_BITMAP_WIDTH = 320;
    
    int maxFaces;
    int faceBitmapWidth;
    
    FaceDetector detector;
    Face[] faces;
    int numFaces;
    int detectorWidth;
    int detectorHeight;
    int bitmapScale;
    PointF facePoint = new PointF();
    
    /**
     * @param maxFaces maximum number of faces to detect in an image
     * @param faceBitmapWidth minimum width of the bitmap to detect faces in. An input image that
     *        has a greater width than this value may be internally resized before detecting faces.
     */
    public FaceFinder(int maxFaces, int faceBitmapWidth) {
        this.maxFaces = maxFaces;
        this.faceBitmapWidth = faceBitmapWidth;
        this.faces = new Face[maxFaces];
    }
    
    /** Calls the 2-argument constructor with default values for maxFaces (10) and faceBitmapWidth (320). */
    public FaceFinder() {
        this(DEFAULT_MAX_FACES, DEFAULT_FACE_BITMAP_WIDTH);
    }
    
    /**
     * Detects faces in data received from the camera preview. After calling this method, the methods
     * getNumberOfFaces, getConfidence, getMidpointX, getMidpointY, and getEyesDistance will return
     * values corresponding to the faces found in the image.
     * 
     * @param data image data in NV21 format, such as data received by Camera.PreviewCallback.onPreviewFrame
     * @param width width of the image
     * @param height height of the image
     * @return number of faces detected in the image
     */
    public int findFacesInCameraData(byte[] data, int width, int height) {
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 90, out);
        byte[] jpegData = out.toByteArray();
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // we don't need a large bitmap to detect faces
        options.inSampleSize = width / faceBitmapWidth;

        Bitmap faceBitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);

        if (detector==null || detectorWidth!=faceBitmap.getWidth() || detectorHeight!=faceBitmap.getHeight()) {
            detector = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), faces.length);
            detectorWidth = faceBitmap.getWidth();
            detectorHeight = faceBitmap.getHeight();
        }
        
        this.numFaces = detector.findFaces(faceBitmap, this.faces);
        bitmapScale = width / faceBitmap.getWidth();
        return numFaces;
    }
    
    /** Returns the number of faces found in the last call to findFacesInCameraData */
    public int getNumberOfFaces() {
        return this.numFaces;
    }
    
    /** Using the faces found in the last call to findFacesInCameraData, returns the confidence value as an int 
     * scaled from 0 to 100 for the given face index. Returns -1 if the index is out of range.
     */
    public int getConfidence(int index) {
        if (index<0 || index>=numFaces) return -1;
        return (int)(100 * this.faces[index].confidence());
    }
    
    /** Using the faces found in the last call to findFacesInCameraData, returns the x position 
     * of the face midpoint for the given face index. Returns -1 if the index is out of range.
     */
    public int getMidpointX(int index) {
        if (index<0 || index>=numFaces) return -1;
        this.faces[index].getMidPoint(facePoint);
        return (int)(bitmapScale * facePoint.x);
    }

    /** Using the faces found in the last call to findFacesInCameraData, returns the x position 
     * of the face midpoint for the given face index. Returns -1 if the index is out of range.
     */
    public int getMidpointY(int index) {
        if (index<0 || index>=numFaces) return -1;
        this.faces[index].getMidPoint(facePoint);
        return (int)(bitmapScale * facePoint.y);
    }

    /** Using the faces found in the last call to findFacesInCameraData, returns the distance 
     * between the eyes for the given face index. Returns -1 if the index is out of range.
     */
    public int getEyesDistance(int index) {
        if (index<0 || index>=numFaces) return -1;
        return (int)(bitmapScale * this.faces[index].eyesDistance());
    }
}
