package org.arguside.core

object CitConstants {

  // flags to enable using "-D..=true" vm arguments
  private[core] final val HeadlessProperty = "citcore.headless"
  private[core] final val NoTimeoutsProperty = "citcore.notimeouts"

  // Eclipse ids

  final val PluginId = "org.argus-ide.cit.core"
  final val EditorId = "argus.tools.eclipse.JawaSourceFileEditor"
  final val ArgusPerspectiveId = "org.argus-ide.cit.core.perspective"

  // project nature
  final val NatureId = "org.argus-ide.cit.core.argusnature"

  // wizards
  final val ClassWizId = "org.scala-ide.sdt.core.wizards.newClass"
  final val ProjectWizId = "org.scala-ide.sdt.core.wizards.newProject"

  // file extensions
  final val PilarFileExtn = ".pilar"
  final val PilarFileExtnShort = ".plr"
  final val JavaFileExtn = ".java"

  final val IssueTracker = "https://github.com/fgwei/argus-ide/issues"
  final val SveltoHomepage = "https://github.com/dragos/svelto"

}
