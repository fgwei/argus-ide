package org.arguside.core.internal.hyperlink

import org.eclipse.jdt.internal.ui.javaeditor.JavaElementHyperlink
import org.eclipse.jdt.ui.actions.OpenAction
import org.eclipse.jface.text.IRegion
import org.eclipse.jface.text.hyperlink.IHyperlink
import org.eclipse.ui.texteditor.ITextEditor
import org.arguside.util.JawaWordFinder
import org.arguside.logging.HasLogger
import org.arguside.core.compiler.InteractiveCompilationUnit
import org.eclipse.jdt.internal.core.Openable
import org.eclipse.jdt.core.IJavaElement
import org.arguside.util.eclipse.RegionUtils._
import org.arguside.core.internal.jdt.search.JawaSelectionRequestor
import org.arguside.core.internal.jdt.search.JawaSelectionEngine

class DeclarationHyperlinkDetector extends BaseHyperlinkDetector with HasLogger {

  protected val resolver: JawaDeclarationHyperlinkComputer = new JawaDeclarationHyperlinkComputer

  override protected def runDetectionStrategy(icu: InteractiveCompilationUnit, textEditor: ITextEditor, currentSelection: IRegion): List[IHyperlink] = {
    val input = textEditor.getEditorInput
    val doc = textEditor.getDocumentProvider.getDocument(input)
    val wordRegion = JawaWordFinder.findWord(doc, currentSelection.getOffset)

    findHyperlinks(textEditor, icu, wordRegion)
  }

  protected def findHyperlinks(textEditor: ITextEditor, icu: InteractiveCompilationUnit, wordRegion: IRegion): List[IHyperlink] = {
    findHyperlinks(textEditor, icu, wordRegion, wordRegion.translate(icu.jawaPos))
  }

  protected def findHyperlinks(textEditor: ITextEditor, icu: InteractiveCompilationUnit, wordRegion: IRegion, mappedRegion: IRegion): List[IHyperlink] = {
    resolver.findHyperlinks(icu, wordRegion, mappedRegion) match {
      case None => List()
      case Some(List()) =>
        icu match {
          case icuOpenable: InteractiveCompilationUnit with Openable =>
            // the following assumes too heavily a Java compilation unit, being based on the dreaded
            // ScalaSelectionEngine. However, this is a last-resort hyperlinking that uses search for
            // top-level types, and unless there are bugs, normal hyperlinking (through compiler symbols)
            // would find it. So we go here only for `JawaCompilationUnit`s.
            javaDeclarationHyperlinkComputer(textEditor, wordRegion, icuOpenable, icuOpenable, mappedRegion)
          case _ =>
            javaDeclarationHyperlinkComputer(textEditor, wordRegion, icu, null, mappedRegion)
        }
      case Some(hyperlinks) =>
        hyperlinks
    }
  }

  private def javaDeclarationHyperlinkComputer(textEditor: ITextEditor, wordRegion: IRegion, icu: InteractiveCompilationUnit, openable: Openable, mappedRegion: IRegion): List[IHyperlink] = {
    try {
      val elements = JavaSelectionEngine.getJavaElements(icu, openable, mappedRegion)

      lazy val qualify = elements.length > 1
      lazy val openAction = new OpenAction(textEditor.getEditorSite()) // changed from asInstanceOf[JavaEditor] to getEditorSite because
      // some editors can be non java editor
      elements.map(new JavaElementHyperlink(wordRegion, openAction, _, qualify))
    } catch {
      case t: Throwable =>
        logger.debug("Exception while computing hyperlink", t)
        Nil
    }
  }
}

object DeclarationHyperlinkDetector {
  def apply(): BaseHyperlinkDetector = new DeclarationHyperlinkDetector
}

/** Helper object to locate Java elements based on a region */
object JavaSelectionEngine extends HasLogger {
  protected[core] def getJavaElements(icu: InteractiveCompilationUnit, openable: Openable, mappedRegion: IRegion): List[IJavaElement] = {
    try {
      val environment = icu.argusProject.newSearchableEnvironment()
      val requestor = new JawaSelectionRequestor(environment.nameLookup, openable)
      val engine = new JawaSelectionEngine(environment, requestor, icu.argusProject.javaProject.getOptions(true))
      val offset = mappedRegion.getOffset
      engine.select(icu, offset, offset + mappedRegion.getLength - 1)
      requestor.getElements.toList
    } catch {
      case t: Throwable =>
        logger.debug("Exception while computing hyperlink", t)
        Nil
    }
  }
}
