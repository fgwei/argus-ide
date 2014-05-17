/**
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package iamandroid.natures;

import iamandroid.AmandroidPlugin;
import iamandroid.core.IModulesManager;
import iamandroid.utils.FileUtils;
import iamandroid.utils.Log;
import iamandroid.utils.MisconfigurationException;

import java.io.File;
import java.util.EmptyStackException;
import java.util.Stack;

import org.eclipse.core.resources.IResource;

public abstract class AbstractPilarNature implements IPilarNature {

    /**
     * @param resource the resource we want info on
     * @return whether the passed resource is in the pythonpath or not (it must be in a source folder for that).
     * @throws MisconfigurationException 
     */
    public boolean isResourceInPythonpath(IResource resource) throws MisconfigurationException {
        return resolveModule(resource) != null;
    }

    public boolean isResourceInPythonpath(String absPath) throws MisconfigurationException {
        return resolveModule(absPath) != null;
    }

    /**
     * @param resource the resource we want to get the name from
     * @return the name of the module in the environment
     * @throws MisconfigurationException 
     */
    public String resolveModule(IResource resource) throws MisconfigurationException {
        String resourceOSString = AmandroidPlugin.getIResourceOSString(resource);
        if (resourceOSString == null) {
            return null;
        }
        return resolveModule(resourceOSString);
    }

    public String resolveModule(File file) throws MisconfigurationException {
        return resolveModule(FileUtils.getFileAbsolutePath(file));
    }

    /**
     * This is a stack holding the modules manager for which the requests were done
     */
    private final Stack<IModulesManager> modulesManagerStack = new Stack<IModulesManager>();
    private final Object modulesManagerStackLock = new Object();

    /**
     * Start a request for an ast manager (start caching things)
     */
//    public boolean startRequests() {
//        ICodeCompletionASTManager astManager = this.getAstManager();
//        if (astManager == null) {
//            return false;
//        }
//        IModulesManager modulesManager = astManager.getModulesManager();
//        if (modulesManager == null) {
//            return false;
//        }
//        synchronized (modulesManagerStackLock) {
//            modulesManagerStack.push(modulesManager);
//            return modulesManager.startCompletionCache();
//        }
//    }

    /**
     * End a request for an ast manager (end caching things)
     */
    public void endRequests() {
        synchronized (modulesManagerStackLock) {
            try {
                IModulesManager modulesManager = modulesManagerStack.pop();
                modulesManager.endCompletionCache();
            } catch (EmptyStackException e) {
                Log.log(e);
            }
        }
    }

}
