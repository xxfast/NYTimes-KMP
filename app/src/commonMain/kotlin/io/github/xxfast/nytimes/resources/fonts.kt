package io.github.xxfast.nytimes.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

val CheltenhamFontFamily: FontFamily @Composable get() = FontFamily(
  font(
    "Cheltenham Light",
    "CheltenhamLight",
    FontWeight.Light,
    FontStyle.Normal
  ),

  font(
    "Cheltenham Normal",
    "CheltenhamNormal",
    FontWeight.Normal,
    FontStyle.Normal
  ),

  font(
    "Cheltenham Bold",
    "CheltenhamBold",
    FontWeight.Bold,
    FontStyle.Normal
  ),
)
