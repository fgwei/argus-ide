/**
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
/*
 * @author: Scott Schlesier
 * Created: March 5, 2005
 */
package amanide.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import amanide.cache.ColorAndStyleCache;
import amanide.preferences.PilarEditorPrefs;

/**
 * 
 * PyColoredScanner is a simple modification to RuleBasedScanner
 * that supports updating the defaultToken color based on a named
 * color in the colorCache
 */
public class PilarCommentScanner extends RuleBasedScanner {
    private ColorAndStyleCache colorCache;
    private String name;

    public PilarCommentScanner(ColorAndStyleCache colorCache, String name) {
        super();
        Assert.isNotNull(colorCache);
        this.colorCache = colorCache;
        this.name = name;
        updateColorAndStyle();
    }

    public void updateColorAndStyle() {
        TextAttribute attr;
        if (PilarEditorPrefs.COMMENT_COLOR.equals(name)) {
            attr = colorCache.getCommentTextAttribute();
        } else {
            throw new RuntimeException("Unexpected: " + name);
        }
        setDefaultReturnToken(new Token(attr));
    }

}