package io.github.xxfast.krouter

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
data class SavedState(val value: Parcelable): Parcelable

@Suppress("UNCHECKED_CAST") // I know what i'm doing
class SavedStateHandle(default: SavedState?): InstanceKeeper.Instance {
  private var savedState: SavedState? = default
  val value: Parcelable? get() = savedState
  fun <T: Parcelable> get(): T? = savedState?.value as? T?
  fun set(value: Parcelable) { this.savedState = SavedState(value) }
  override fun onDestroy() { savedState = null }
}
