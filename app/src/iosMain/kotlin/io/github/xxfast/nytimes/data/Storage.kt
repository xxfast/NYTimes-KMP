package io.github.xxfast.nytimes.data

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.github.xxfast.nytimes.di.appStorageDir
import io.github.xxfast.nytimes.models.SavedArticles
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.dataUsingEncoding

// TODO: https://github.com/xxfast/KStore/issues/44
actual val store: KStore<SavedArticles> by lazy {
  val fileManager: NSFileManager = NSFileManager.defaultManager
  val appDir = requireNotNull(appStorageDir) { "appStorageDir not set" }
  val directoryUrl: NSURL = NSURL.fileURLWithPath(appDir)
  val subPath = "saved.json"
  val fileUrl: NSURL = directoryUrl.URLByAppendingPathComponent(subPath)!!
  val filePath: String = fileUrl.path!!

  if (!fileManager.fileExistsAtPath(filePath)) {
    fileManager.createFileAtPath(
      filePath,
      ("[]" as NSString).dataUsingEncoding(NSUTF8StringEncoding),
      null
    )
  }

  storeOf(filePath, emptySet())
}
