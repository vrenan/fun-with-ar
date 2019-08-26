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

import android.content.Context
import android.util.Log
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable

import java.util.concurrent.CompletableFuture

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
class AugmentedImageNode(context: Context) : AnchorNode() {

    // The augmented image represented by this node.
    /**
     * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
     * created based on an Anchor created from the image. The corners are then positioned based on the
     * extents of the image. There is no need to worry about world coordinates since everything is
     * relative to the center of the image, which is the parent node of the corners.
     */
    // If any of the models are not loaded, then recurse when all are loaded.
    // Set the anchor based on the center of the image.
    // Make the 4 corner nodes.
    // Upper left corner.
    // Make sure the longest edge fits inside the image.
    var image: AugmentedImage? = null
        set(image) {
            field = image
            if (!viewRenderableFuture!!.isDone) {
                CompletableFuture.allOf(viewRenderableFuture)
                        .thenAccept { this.image = image }
                        .exceptionally { throwable ->
                            Log.e(TAG, "Exception loading", throwable)
                            null
                        }
            }
            anchor = image?.createAnchor(image.centerPose)
            val localPosition = Vector3()
            val cornerNode: Node
            cornerNode = Node()
            val maze_edge_size = 0.5f
            val max_image_edge = Math.max(image!!.getExtentX(), image!!.getExtentZ())
            maze_scale = max_image_edge / maze_edge_size

            cornerNode.localScale = Vector3(maze_scale * 0.8f, maze_scale * 0.8f, maze_scale * 0.8f)
            cornerNode.localRotation = Quaternion(Vector3(1f, 0f, 0f), -90f)
            cornerNode.setParent(this)
            cornerNode.renderable = viewRenderableFuture!!.getNow(null)

        }
    private var maze_scale = 0.0f

    init {
        // Upon construction, start loading the models for the corners of the frame.
        if (viewRenderableFuture == null) {
            viewRenderableFuture = ViewRenderable.builder().setView(context, R.layout.solar_controls)
                    .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                    .build()
        }
    }

    companion object {

        private val TAG = "AugmentedImageNode"
        // Models of the 4 corners.  We use completable futures here to simplify
        // the error handling and asynchronous loading.  The loading is started with the
        // first construction of an instance, and then used when the image is set.
        private var viewRenderableFuture: CompletableFuture<ViewRenderable>? = null
    }
}
