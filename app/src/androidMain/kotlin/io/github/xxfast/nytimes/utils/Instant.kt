package io.github.xxfast.nytimes.utils

import android.os.Parcel
import com.arkivanov.essenty.parcelable.Parceler
import kotlinx.datetime.Instant

actual object InstantParceler: Parceler<Instant> {
  override fun create(parcel: Parcel): Instant = Instant.fromEpochMilliseconds(parcel.readLong())
  override fun Instant.write(parcel: Parcel, flags: Int) { parcel.writeLong(epochSeconds) }
}
