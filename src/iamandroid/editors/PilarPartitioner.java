/**
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
/*
 * Created on Jun 27, 2005
 * 
 * @author Fabio Zadrozny
 */
package iamandroid.editors;

import org.eclipse.jface.text.rules.IPartitionTokenScanner;

/**
 * @author Fabio Zadrozny
 */
public final class PilarPartitioner extends org.eclipse.jface.text.rules.FastPartitioner {

    /**
     * @param scanner
     * @param legalContentTypes
     */
    public PilarPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
        super(scanner, legalContentTypes);
    }

    public IPartitionTokenScanner getScanner() {
        return fScanner;
    }

}
