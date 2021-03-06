package org.arguside.util.internal.ui

trait UIThread {
  /** Asynchronously run `f` on the UI thread.  */
  def asyncExec(f: => Unit): Unit

  /** Synchronously run `f` on the UI thread.  */
  def syncExec(f: => Unit): Unit

  /** Retrieve the UI Thread instance */
  private [arguside] def get: Thread
}
