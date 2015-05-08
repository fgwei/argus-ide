//package org.arguside.core.internal.hyperlink
//
//import org.eclipse.jface.text.IRegion
//import org.eclipse.jface.text.hyperlink.IHyperlink
//import org.arguside.logging.HasLogger
//import org.arguside.core.compiler.InteractiveCompilationUnit
//import org.arguside.core.compiler.IArgusPresentationCompiler.Implicits._
//
//class JawaDeclarationHyperlinkComputer extends HasLogger {
//  def findHyperlinks(icu: InteractiveCompilationUnit, wordRegion: IRegion): Option[List[IHyperlink]] = {
//    findHyperlinks(icu, wordRegion, wordRegion)
//  }
//
//  def findHyperlinks(icu: InteractiveCompilationUnit, wordRegion: IRegion, mappedRegion: IRegion): Option[List[IHyperlink]] = {
//    logger.info("detectHyperlinks: wordRegion = " + mappedRegion)
//
//    icu.withSourceFile({ (sourceFile, compiler) =>
//      if (mappedRegion == null || mappedRegion.getLength == 0)
//        None
//      else {
//        val start = mappedRegion.getOffset
//        val regionEnd = mappedRegion.getOffset + mappedRegion.getLength
//        // removing 1 handles correctly hyperlinking requests @ EOF
//        val end = if (sourceFile.length == regionEnd) regionEnd - 1 else regionEnd
//
//        val pos = compiler.rangePos(sourceFile, start, start, end)
//
//        import compiler.{ log => _, _ }
//
//        val typed = askTypeAt(pos).getOption()
//
//        val symsOpt: Option[List[(Symbol,String)]] = compiler.asyncExec {
//          val targetsOpt = typed map {
//            case Import(expr, sels) =>
//              if (expr.pos.includes(pos)) {
//                @annotation.tailrec
//                def locate(p: Position, inExpr: Tree): Symbol = inExpr match {
//                  case Select(qualifier, name) =>
//                    if (qualifier.pos.includes(p)) locate(p, qualifier)
//                    else inExpr.symbol
//                  case tree => tree.symbol
//                }
//
//                List(locate(pos, expr))
//              } else {
//                sels find (selPos => selPos.namePos >= pos.start && selPos.namePos <= pos.end) map { sel =>
//                  val tpe = stabilizedType(expr)
//                  List(tpe.member(sel.name), tpe.member(sel.name.toTypeName))
//                } getOrElse Nil
//              }
//            case Annotated(atp, _)                                => List(atp.symbol)
//            case Literal(const) if const.tag == compiler.ClazzTag => List(const.typeValue.typeSymbol)
//            case ap @ Select(qual, nme.apply)                     => List(ap.symbol, qual.symbol)
//            case st if st.symbol ne null                          => List(st.symbol)
//            case _                                                => List()
//          } map (_.filterNot{ sym => sym == NoSymbol || sym.hasPackageFlag || sym.isJavaDefined })
//
//          for {
//            targets <- targetsOpt.toList
//            target <- targets
//          } yield (target -> target.toString)
//        }.getOption()
//
//        symsOpt map { syms =>
//          syms flatMap {
//            case (sym, symName) => compiler.mkHyperlink(sym, s"Open Declaration (${symName})", wordRegion, icu.argusProject.javaProject).toList
//          }
//        }
//      }
//    }).flatten
//  }
//
//}
