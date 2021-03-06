package argus.tools.eclipse.contribution.weaving.jdt.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.SourceTypeConverter;
import org.eclipse.jdt.internal.core.SourceAnnotationMethodInfo;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceMethodElementInfo;
import org.eclipse.jdt.internal.core.SourceType;

@SuppressWarnings("restriction")
public privileged aspect DOMAspect {
  pointcut internalCreateAST(ASTParser parser, IProgressMonitor monitor) :
    execution(ASTNode ASTParser.internalCreateAST(IProgressMonitor)) &&
    args(monitor) &&
    target(parser);
  
  pointcut convert(SourceTypeConverter stc, SourceMethod methodHandle, SourceMethodElementInfo methodInfo, CompilationResult compilationResult) :
    execution(AbstractMethodDeclaration SourceTypeConverter.convert(SourceMethod, SourceMethodElementInfo, CompilationResult)) &&
    args(methodHandle, methodInfo, compilationResult) &&
    target(stc);

  AbstractMethodDeclaration around(SourceTypeConverter stc, SourceMethod methodHandle, SourceMethodElementInfo methodInfo, CompilationResult compilationResult) throws JavaModelException :
    convert(stc, methodHandle, methodInfo, compilationResult) {
    AbstractMethodDeclaration method;

    /* only source positions available */
    int start = methodInfo.getNameSourceStart();
    int end = methodInfo.getNameSourceEnd();

    // convert 1.5 specific constructs only if compliance is 1.5 or above
    TypeParameter[] typeParams = null;
    if (stc.has1_5Compliance) {
      /* convert type parameters */
      char[][] typeParameterNames = methodInfo.getTypeParameterNames();
      if (typeParameterNames != null) {
        int parameterCount = typeParameterNames.length;
        if (parameterCount > 0) { // method's type parameters must be null if no type parameter
          char[][][] typeParameterBounds = methodInfo.getTypeParameterBounds();
          typeParams = new TypeParameter[parameterCount];
          for (int i = 0; i < parameterCount; i++) {
            typeParams[i] = stc.createTypeParameter(typeParameterNames[i], typeParameterBounds[i], start, end);
          }
        }
      }
    }

    int modifiers = methodInfo.getModifiers();
    if (methodInfo.isConstructor()) {
      ConstructorDeclaration decl = new ConstructorDeclaration(compilationResult);
      decl.bits &= ~org.eclipse.jdt.internal.compiler.ast.ASTNode.IsDefaultConstructor;
      method = decl;
      decl.typeParameters = typeParams;
    } else {
      MethodDeclaration decl;
      if (methodInfo.isAnnotationMethod()) {
        AnnotationMethodDeclaration annotationMethodDeclaration = new AnnotationMethodDeclaration(compilationResult);

        /* conversion of default value */
        SourceAnnotationMethodInfo annotationMethodInfo = (SourceAnnotationMethodInfo) methodInfo;
        boolean hasDefaultValue = annotationMethodInfo.defaultValueStart != -1 || annotationMethodInfo.defaultValueEnd != -1;
        if ((stc.flags & SourceTypeConverter.FIELD_INITIALIZATION) != 0) {
          if (hasDefaultValue) {
            char[] defaultValueSource = CharOperation.subarray(stc.getSource(), annotationMethodInfo.defaultValueStart, annotationMethodInfo.defaultValueEnd+1);
            if (defaultValueSource != null) {
                Expression expression =  stc.parseMemberValue(defaultValueSource);
                if (expression != null) {
                  annotationMethodDeclaration.defaultValue = expression;
                }
            } else {
              // could not retrieve the default value
              hasDefaultValue = false;
            }
          }
        }
        if (hasDefaultValue)
          modifiers |= ClassFileConstants.AccAnnotationDefault;
        decl = annotationMethodDeclaration;
      } else {
        decl = new MethodDeclaration(compilationResult);
      }

      // convert return type
      decl.returnType = stc.createTypeReference(methodInfo.getReturnTypeName(), start, end);

      // type parameters
      decl.typeParameters = typeParams;

      method = decl;
    }
    method.selector = methodHandle.getElementName().toCharArray();
    boolean isVarargs = (modifiers & ClassFileConstants.AccVarargs) != 0;
    method.modifiers = modifiers & ~ClassFileConstants.AccVarargs;
    method.sourceStart = start;
    method.sourceEnd = end;
    method.declarationSourceStart = methodInfo.getDeclarationSourceStart();
    method.declarationSourceEnd = methodInfo.getDeclarationSourceEnd();

    // convert 1.5 specific constructs only if compliance is 1.5 or above
    if (stc.has1_5Compliance) {
      /* convert annotations */
      method.annotations = stc.convertAnnotations(methodHandle);
    }

    /* convert arguments */
    String[] argumentTypeSignatures = methodHandle.getParameterTypes();
    char[][] argumentNames = methodInfo.getArgumentNames();
    int argumentCount = argumentTypeSignatures == null ? 0 : argumentTypeSignatures.length;
    if (argumentCount > 0) {
      long position = ((long) start << 32) + end;
      method.arguments = new Argument[argumentCount];
      for (int i = 0; i < argumentCount; i++) {
        TypeReference typeReference = stc.createTypeReference(argumentTypeSignatures[i], start, end);
        if (isVarargs && i == argumentCount-1) {
          typeReference.bits |= org.eclipse.jdt.internal.compiler.ast.ASTNode.IsVarArgs;
        }
        method.arguments[i] =
          new Argument(
            argumentNames[i],
            position,
            typeReference,
            ClassFileConstants.AccDefault);
        // do not care whether was final or not
      }
    }

    /* convert thrown exceptions */
    char[][] exceptionTypeNames = methodInfo.getExceptionTypeNames();
    int exceptionCount = exceptionTypeNames == null ? 0 : exceptionTypeNames.length;
    if (exceptionCount > 0) {
      method.thrownExceptions = new TypeReference[exceptionCount];
      for (int i = 0; i < exceptionCount; i++) {
        method.thrownExceptions[i] =
          stc.createTypeReference(exceptionTypeNames[i], start, end);
      }
    }

    /* convert local and anonymous types */
    if ((stc.flags & SourceTypeConverter.LOCAL_TYPE) != 0) {
      IJavaElement[] children = methodInfo.getChildren();
      int typesLength = 0;
      int childrenLength = children.length;
      for (int i = 0; i < childrenLength; ++i)
        if (children[i] instanceof SourceType)
          ++typesLength;
      
      if (typesLength != 0) {
        Statement[] statements = new Statement[typesLength];
        int typeIndex = 0;
        for (int i = 0; i < childrenLength; i++) {
          if (children[i] instanceof SourceType) {
            SourceType type = (SourceType) children[i];
            TypeDeclaration localType = stc.convert(type, compilationResult);
            if ((localType.bits & org.eclipse.jdt.internal.compiler.ast.ASTNode.IsAnonymousType) != 0) {
              QualifiedAllocationExpression expression = new QualifiedAllocationExpression(localType);
              expression.type = localType.superclass;
              localType.superclass = null;
              localType.superInterfaces = null;
              localType.allocation = expression;
              statements[typeIndex] = expression;
            } else {
              statements[typeIndex] = localType;
            }
            ++typeIndex;
          }
        }
        method.statements = statements;
      }
    }

    return method;
  }
}
