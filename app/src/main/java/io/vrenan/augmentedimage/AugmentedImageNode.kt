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

import android.content.Context
import android.util.Log
import android.view.View
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable

import java.util.concurrent.CompletableFuture
import kotlin.math.max

class AugmentedImageNode(context: Context) : AnchorNode() {
    val animationView: WinkAnimationView?
        get() {
        return winkRenderable?.view as WinkAnimationView
        }

    private  var winkRenderable: ViewRenderable? = null
    var image: AugmentedImage? = null
        set(image) {
            field = image
            if (!viewRenderableFuture!!.isDone) {
                CompletableFuture.allOf(viewRenderableFuture)
                        .thenAccept {
                            this.image = image
                        }
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
            val max_image_edge = max(image!!.extentX, image.extentZ)
            maze_scale = max_image_edge / maze_edge_size

            cornerNode.localScale = Vector3(maze_scale * 0.8f, maze_scale * 0.8f, maze_scale * 0.8f)
            cornerNode.localRotation = Quaternion(Vector3(1f, 0f, 0f), -90f)
            cornerNode.setParent(this)
            winkRenderable = viewRenderableFuture!!.getNow(null)
            cornerNode.renderable = winkRenderable

        }
    private var maze_scale = 0.0f

    init {
        // Upon construction, start loading the models for the corners of the frame.
        if (viewRenderableFuture == null) {
            viewRenderableFuture = ViewRenderable.builder()
                    .setView(context, WinkAnimationView(context))
                    .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                    .build()
        }
    }

    companion object {

        private val TAG = "AugmentedImageNode"

        private var viewRenderableFuture: CompletableFuture<ViewRenderable>? = null
    }
}
