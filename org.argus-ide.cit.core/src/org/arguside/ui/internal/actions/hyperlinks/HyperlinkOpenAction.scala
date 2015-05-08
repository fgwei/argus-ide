package org.arguside.ui.internal.actions.hyperlinks

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor
import org.eclipse.jdt.ui.actions.OpenAction
import org.arguside.core.internal.hyperlink.BaseHyperlinkDetector

class HyperlinkOpenAction(editor: JavaEditor) extends OpenAction(editor) with HyperlinkOpenActionStrategy {

//  override protected val detectionStrategy: BaseHyperlinkDetector = DeclarationHyperlinkDetector()

//  override def run() { openHyperlink(editor) }
}
