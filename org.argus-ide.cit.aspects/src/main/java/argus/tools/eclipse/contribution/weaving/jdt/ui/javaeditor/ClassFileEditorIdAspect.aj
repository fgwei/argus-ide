package argus.tools.eclipse.contribution.weaving.jdt.ui.javaeditor;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.ui.IEditorInput;

import argus.tools.eclipse.contribution.weaving.jdt.IJawaClassFile;

@SuppressWarnings("restriction")
public aspect ClassFileEditorIdAspect {
  pointcut getEditorID(IEditorInput input) :
    args(input) &&
    execution(String EditorUtility.getEditorID(IEditorInput));

  String around(IEditorInput input) :
    getEditorID(input) {
    if (input instanceof IClassFileEditorInput) {
      IClassFile cf = ((IClassFileEditorInput)input).getClassFile();
      if (cf instanceof IJawaClassFile)
        return "argus.tools.eclipse.PilarClassFileEditor";
    }
    
    return proceed(input);
  }
}
