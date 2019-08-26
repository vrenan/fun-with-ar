/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage

import android.app.ActivityManager
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session

import com.google.ar.sceneform.samples.common.helpers.SnackbarHelper
import com.google.ar.sceneform.ux.ArFragment
import java.io.IOException
import java.io.InputStream

/**
 * Extend the ArFragment to customize the ARCore session configuration to include Augmented Images.
 */
class AugmentedImageFragment : ArFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val openGlVersionString = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later")
            SnackbarHelper.getInstance()
                    .showError(activity, "Sceneform requires OpenGL ES 3.0 or later")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false

        return view
    }

    protected override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)
        if (!setupAugmentedImageDatabase(config, session)) {
            SnackbarHelper.getInstance()
                    .showError(activity, "Could not setup augmented image database")
        }
        return config
    }

    private fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {
        var augmentedImageDatabase: AugmentedImageDatabase? = null

        val assetManager = if (context != null) context?.assets else null

        if (assetManager == null) {
            Log.e(TAG, "Context is null, cannot intitialize image database.")
            return false
        }

        try {
            context?.assets?.open(SAMPLE_IMAGE_DATABASE).use { `is` -> augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, `is`) }
        } catch (e: IOException) {
            Log.e(TAG, "IO exception loading augmented image database.", e)
            return false
        }

        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    private fun loadAugmentedImageBitmap(assetManager: AssetManager): Bitmap? {
        try {
            assetManager.open(DEFAULT_IMAGE_NAME).use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e)
        }

        return null
    }

    companion object {
        private val TAG = "AugmentedImageFragment"

        // This is the name of the image in the sample database.  A copy of the image is in the assets
        // directory.  Opening this image on your computer is a good quick way to test the augmented image
        // matching.
        private val DEFAULT_IMAGE_NAME = "default.jpg"

        // This is a pre-created database containing the sample image.
        private val SAMPLE_IMAGE_DATABASE = "sample_database.imgdb"

        // Augmented image configuration and rendering.
        // Load a single image (true) or a pre-generated image database (false).
        private val USE_SINGLE_IMAGE = false

        // Do a runtime check for the OpenGL level available at runtime to avoid Sceneform crashing the
        // application.
        private val MIN_OPENGL_VERSION = 3.0
    }
}
