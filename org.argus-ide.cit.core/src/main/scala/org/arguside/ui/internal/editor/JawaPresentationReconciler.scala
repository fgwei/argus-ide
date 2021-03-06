package org.arguside.ui.internal.editor

import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.IDocument
import org.eclipse.jface.text.IRegion
import org.eclipse.jface.text.TextPresentation
import org.eclipse.jface.text.presentation.PresentationReconciler

class JawaPresentationReconciler extends PresentationReconciler {

  @volatile private var lastDocument: IDocument = null

  def createRepairDescription(damage: IRegion, document: IDocument): TextPresentation = {
    if (document != lastDocument) {
      setDocumentToDamagers(document)
      setDocumentToRepairers(document)
      lastDocument = document
    }
    createPresentation(damage, document)
  }
}
