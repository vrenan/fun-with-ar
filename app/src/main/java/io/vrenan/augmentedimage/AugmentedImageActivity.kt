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

package io.vrenan.augmentedimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import io.vrenan.augmentedimage.helpers.SnackbarHelper
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.augmented_image_activity.*
import kotlinx.android.synthetic.main.augmented_image_activity.view.*
import java.util.HashMap

class AugmentedImageActivity : AppCompatActivity() {

    private var arFragment: ArFragment? = null
    private var fitToScanView: ImageView? = null

    private val augmentedImageMap = HashMap<AugmentedImage, AugmentedImageNode>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.augmented_image_activity)

        arFragment = ux_fragment as ArFragment?
        fitToScanView = image_view_fit_to_scan

        arFragment!!.arSceneView.scene.addOnUpdateListener { this.onUpdateFrame(it) }
    }

    override fun onResume() {
        super.onResume()
        if (augmentedImageMap.isEmpty()) {
            fitToScanView!!.visibility = View.VISIBLE
            toolbar!!.visibility = View.INVISIBLE
        }
    }

    private fun onUpdateFrame(frameTime: FrameTime) {
        val frame = arFragment!!.arSceneView.arFrame ?: return

        val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
        for (augmentedImage in updatedAugmentedImages) {
            when (augmentedImage.trackingState) {
                TrackingState.PAUSED -> {
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                }

                TrackingState.TRACKING -> {
                    // Have to switch to UI Thread to update View.
                    fitToScanView!!.visibility = View.GONE
                    toolbar.visibility = View.VISIBLE
                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        val node = AugmentedImageNode(this)
                        node.image = augmentedImage
                        toolbar.button.setOnClickListener {
                            node.animationView?.setState(WinkAnimationView.State.ENABLED)
                        }
                        augmentedImageMap[augmentedImage] = node
                        arFragment!!.arSceneView.scene.addChild(node)
                    }
                }

                TrackingState.STOPPED -> augmentedImageMap.remove(augmentedImage)
            }
        }
    }
}
