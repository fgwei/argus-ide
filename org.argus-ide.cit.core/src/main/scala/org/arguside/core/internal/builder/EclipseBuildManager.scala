package org.arguside.core.internal.builder

import org.eclipse.core.resources.IFile
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.SubMonitor

/**
 * Abstraction which exposes jawa compiler to eclipse.
 */
trait EclipseBuildManager {
  def build(addedOrUpdated: Set[IFile], removed: Set[IFile], monitor: SubMonitor): Unit

  /** Has build errors? Only valid if the project has been built before. */
  @volatile protected var hasInternalErrors: Boolean = false

  /** <code>true</code> says that compiler requires a sources reload. */
  def invalidateAfterLoad: Boolean

  /** Can be used to clean an compiler's internal state. */
  def clean(implicit monitor: IProgressMonitor): Unit

  /** Says about a compilation result. */
  def hasErrors: Boolean = hasInternalErrors

  /** Says if underlying compiler is able to find out and add dependencies to build path. */
  def canTrackDependencies: Boolean

  /** Gives back the latest dependencies analysis done by underlying compiler. */
//  def latestAnalysis(incOptions: => IncOptions): Analysis
}

/** Keeps collected analysis persistently in store. This store is exposed outdoor. */
trait CachedAnalysisBuildManager extends EclipseBuildManager {
  def analysisStore: IFile
}