package com.eygraber.cares.samples.torrents

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.singleWindowApplication
import com.eygraber.cares.samples.torrents.main.MainRenderNode
import javax.swing.JOptionPane
import javax.swing.UIManager

fun main() {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  singleWindowApplication(title = "Torrents") {
    MaterialTheme(
      colors = darkColors(
        primary = Color(0xFFBB86FC),
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC5)
      )
    ) {
      Menu()

      Torrents()
    }
  }
}

@Composable
private fun FrameWindowScope.Menu() {
  var dialogText by remember { mutableStateOf<String?>(null) }
  var showDialog by remember { mutableStateOf(false) }

  if(showDialog) {
    Dialog(
      onCloseRequest = { showDialog = false }
    ) {
      Text(dialogText ?: "Nothing to show")
    }
  }

  MenuBar {
    Menu("File") {
      Item(
        "Copy",
        onClick = {
          dialogText = "Copied"
          showDialog = true
        }
      )
    }

    Menu("Edit") {
      Item("Preferences", onClick = { JOptionPane.showMessageDialog(null, "Preferences") })
    }

    Menu("View") {
      Item("Sidebar", onClick = { JOptionPane.showMessageDialog(null, "Sidebar") })
    }

    Menu("Help") {
      Item("FAQ", onClick = { JOptionPane.showMessageDialog(null, "FAQ") })
    }
  }
}

@Composable
private fun Torrents() {
  val renderNode = remember { MainRenderNode() }
  renderNode.render()
}
