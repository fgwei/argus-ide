package org.arguside.core.internal.quickassist

import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation
import org.eclipse.jdt.ui.text.java.IInvocationContext
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal
import org.eclipse.jdt.ui.text.java.IProblemLocation
import org.eclipse.jface.text.IDocument
import org.arguside.core.quickassist.AssistLocation
import org.arguside.core.quickassist.BasicCompletionProposal
import org.arguside.core.quickassist.InvocationContext

/**
 * Adapter for [[org.arguside.core.quickassist.InvocationContext]] that
 * implements [[org.eclipse.jdt.ui.text.java.IInvocationContext]].
 */
final class JavaInvocationContextAdapter(ctx: InvocationContext) extends IInvocationContext {
  override def getCompilationUnit = ctx.icu.asInstanceOf[ICompilationUnit]
  override def getSelectionOffset = ctx.selectionStart
  override def getSelectionLength = ctx.selectionLength
  override def getASTRoot = null
  override def getCoveredNode = null
  override def getCoveringNode = null

  def javaProblemLocations: Array[IProblemLocation] =
    ctx.problemLocations.collect {
      case AssistLocation(offset, length, annotation: IJavaAnnotation) =>
        new ProblemLocation(offset, length, annotation)
    }.toArray
}

/**
 * Adapter for [[org.eclipse.jdt.ui.text.java.IJavaCompletionProposal]] that
 * implements [[org.arguside.core.quickassist.BasicCompletionProposal]].
 */
final case class JavaProposalAdapter(jcp: IJavaCompletionProposal)
    extends BasicCompletionProposal(
      relevance = jcp.getRelevance,
      displayString = jcp.getDisplayString,
      image = jcp.getImage) {

  override def apply(document: IDocument): Unit =
    jcp.apply(document)
}
