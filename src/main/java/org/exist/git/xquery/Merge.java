/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  $Id$
 */
package org.exist.git.xquery;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.util.FS_eXistdb;
import org.exist.dom.QName;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Merge extends BasicFunction {

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			new QName("merge", Module.NAMESPACE_URI, Module.PREFIX), 
			"", 
			new SequenceType[] { 
                new FunctionParameterSequenceType(
                    "localPath", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "Local path"
                ),
                new FunctionParameterSequenceType(
                    "branch-name", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "The name of the branch"
                )
//                ,
//                new FunctionParameterSequenceType(
//                    "create", 
//                    Type.BOOLEAN, 
//                    Cardinality.EXACTLY_ONE, 
//                    "Create new branch"
//                )
			}, 
			new FunctionReturnSequenceType(
				Type.BOOLEAN, 
				Cardinality.EXACTLY_ONE, 
				"true if success, false otherwise"
			)
		)
	};

	public Merge(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
            String localPath = args[0].getStringValue();
            if (!(localPath.endsWith("/")))
                localPath += File.separator;

	        Git git = Git.open(new Resource(localPath), new FS_eXistdb());
		    
	        MergeResult res = git.merge()
//	                .include("foo")
	                .call(); // actually do the merge

	        return new StringValue(res.getMergeStatus().toString() );
//	        .equals(MergeResult.MergeStatus.CONFLICTING)){
//	           System.out.println(res.getConflicts().toString());
//	        }
		} catch (Throwable e) {
			throw new XPathException(this, Module.EXGIT001, e);
		}
	}
}