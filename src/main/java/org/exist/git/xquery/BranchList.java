/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2012-2013 The eXist Project
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

import static org.exist.git.xquery.Module.FS;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.exist.dom.QName;
import org.exist.util.io.Resource;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

/**
 * 
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class BranchList extends BasicFunction {

	public final static FunctionSignature signatures[] = { 
		new FunctionSignature(
			new QName("branch-list", Module.NAMESPACE_URI, Module.PREFIX), 
			"", 
			new SequenceType[] { 
                new FunctionParameterSequenceType(
                    "localPath", 
                    Type.STRING, 
                    Cardinality.EXACTLY_ONE, 
                    "Local path"
                )
			}, 
			new FunctionReturnSequenceType(
				Type.STRING, 
				Cardinality.ZERO_OR_ONE, 
				"Branchs' name."
			)
		)
	};

	public BranchList(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

	@Override
	public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {

		try {
            String localPath = args[0].getStringValue();
            if (!(localPath.endsWith("/")))
                localPath += File.separator;

	        Git git = Git.open(new Resource(localPath), FS);
		    
	        Sequence result = new ValueSequence();
	        List<Ref> list = git.branchList().call();
	        for (Ref ref : list) {
	            result.add(new StringValue(ref.getName()));
	        }

	        return result;
		} catch (Throwable e) {
			throw new XPathException(this, Module.EXGIT001, e);
		}
	}
}