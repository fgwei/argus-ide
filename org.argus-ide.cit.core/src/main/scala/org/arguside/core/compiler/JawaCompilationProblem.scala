package org.arguside.core.compiler

import org.eclipse.jdt.core.compiler.IProblem
import org.eclipse.core.resources.IFile
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem

/** A Jawa error or warning.
 *
 *  @param fileName   The corresponding file name
 *  @param severity   One of ProblemSeverities.{Error, Warning, Ignore}
 *  @param message    The error message emitted by the compiler
 *  @param start      The starting offset of where this problem is located
 *  @param end        The end offset of this problem
 *  @param lineNumber The line number of this problem
 *  @param column     The column number of this problem
 */
case class JawaCompilationProblem(fileName: String, severity: Int, message: String, start: Int, end: Int, lineNumber: Int, columnNumber: Int)
  extends DefaultProblem(fileName.toCharArray(),
    message,
    0,
    Array.empty[String],
    severity,
    start,
    end,
    lineNumber,
    columnNumber) {
  override def toString() =
    s"$fileName:$lineNumber [$start:$end] $message"
}
