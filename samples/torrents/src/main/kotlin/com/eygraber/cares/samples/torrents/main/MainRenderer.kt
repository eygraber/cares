package com.eygraber.cares.samples.torrents.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eygraber.cares.Emitter
import com.eygraber.cares.RenderNode
import com.eygraber.cares.Renderer

class MainRenderer(
  private val controlsRenderNode: RenderNode<*, *>,
  private val dataTableRenderNode: RenderNode<*, *>,
  private val statsRenderNode: RenderNode<*, *>,
  private val footerRenderNode: RenderNode<*, *>
) : Renderer<Unit, Unit> {
  @Composable
  override fun render(state: Unit, emitEvent: Emitter<Unit>) {
    Surface {
      Column(
        modifier = Modifier
          .fillMaxSize()
      ) {
        controlsRenderNode.render()

        Box(
          modifier = Modifier
            .weight(.95F)
        ) {
          dataTableRenderNode.render()
          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
          ) {
            statsRenderNode.render()
          }
        }

        Box(
          modifier = Modifier
            .weight(.05F)
        ) {
          footerRenderNode.render()
        }
      }
    }
  }
}
