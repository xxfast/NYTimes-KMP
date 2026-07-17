package io.github.xxfast.nytimes.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.unit.dp

// TODO: Figure out what the actual insets are here
actual val WindowInsets.Companion.statusBarPadding: WindowInsets get() = WindowInsets(top = 48.dp)

actual val WindowInsets.Companion.navigationBarPadding: WindowInsets get() = WindowInsets(bottom = 8.dp)
