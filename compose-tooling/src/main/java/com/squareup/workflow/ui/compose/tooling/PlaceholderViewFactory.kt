/*
 * Copyright 2020 Square Inc.
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
@file:Suppress("SameParameterValue", "DEPRECATION")

package com.squareup.workflow.ui.compose.tooling

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.clipToBounds
import androidx.ui.core.drawBehind
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBorder
import androidx.ui.geometry.Offset
import androidx.ui.geometry.toRect
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.Shadow
import androidx.ui.graphics.drawscope.DrawScope
import androidx.ui.graphics.drawscope.Stroke
import androidx.ui.graphics.drawscope.drawCanvas
import androidx.ui.graphics.drawscope.rotate
import androidx.ui.graphics.withSaveLayer
import androidx.ui.layout.fillMaxSize
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.squareup.workflow.ui.ViewFactory
import com.squareup.workflow.ui.compose.composedViewFactory

/**
 * A [ViewFactory] that will be used any time a [PreviewViewRegistry] is asked to show a rendering.
 * It displays a placeholder graphic and the rendering's `toString()` result.
 */
internal fun placeholderViewFactory(modifier: Modifier): ViewFactory<Any> =
  composedViewFactory { rendering, _ ->
    Text(
        modifier = modifier
            .clipToBounds()
            .drawBehind {
              drawCanvas { canvas, size ->
                canvas.withSaveLayer(size.toRect(), Paint().apply { alpha = .2f }) {
                  canvas.drawRect(size.toRect(), Paint().apply { color = Color.Gray })
                  drawCrossHatch(
                      color = Color.Red,
                      strokeWidth = 2.dp,
                      spaceWidth = 5.dp,
                      angle = 45f
                  )
                }
              }
            },
        text = rendering.toString(),
        style = TextStyle(
            textAlign = TextAlign.Center,
            color = Color.White,
            shadow = Shadow(blurRadius = 5f, color = Color.Black)
        )
    )
  }

@Preview(widthDp = 200, heightDp = 200)
@Composable private fun PreviewStubViewBindingOnWhite() {
  Box(backgroundColor = Color.White) {
    PreviewStubBindingPreviewTemplate()
  }
}

@Preview(widthDp = 200, heightDp = 200)
@Composable private fun PreviewStubViewBindingOnBlack() {
  Box(backgroundColor = Color.Black) {
    PreviewStubBindingPreviewTemplate()
  }
}

@Composable private fun PreviewStubBindingPreviewTemplate() {
  placeholderViewFactory(Modifier).preview(
      rendering = "preview",
      placeholderModifier = Modifier.fillMaxSize()
          .drawBorder(size = 1.dp, color = Color.Red)
  )
}

private fun DrawScope.drawCrossHatch(
  color: Color,
  strokeWidth: Dp,
  spaceWidth: Dp,
  angle: Float
) {
  drawHatch(color, strokeWidth, spaceWidth, angle)
  drawHatch(color, strokeWidth, spaceWidth, angle + 90)
}

private fun DrawScope.drawHatch(
  color: Color,
  strokeWidth: Dp,
  spaceWidth: Dp,
  angle: Float
) {
  val strokeWidthPx = strokeWidth.toPx()
  val spaceWidthPx = spaceWidth.toPx()
  val strokeColor = color.scaleColors(.5f)

  rotate(angle) {
    // Draw outside our bounds to fill the space even when rotated.
    val left = -size.width
    val right = size.width * 2
    val top = -size.height
    val bottom = size.height * 2

    var y = top + strokeWidthPx * 2f
    while (y < bottom) {
      drawLine(
          strokeColor,
          Offset(left, y),
          Offset(right, y),
          strokeWidth = strokeWidthPx
      )
      y += spaceWidthPx * 2
    }
  }
}

private fun Color.scaleColors(factor: Float) =
  copy(red = red * factor, green = green * factor, blue = blue * factor)
