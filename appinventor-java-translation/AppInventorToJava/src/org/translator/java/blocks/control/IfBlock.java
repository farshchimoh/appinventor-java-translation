/*
   appinventor-java-translation

   Originally authored by Joshua Swank at the University of Alabama
   Work supported in part by NSF award #0702764 and a Google 2011 CS4HS award

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package org.translator.java.blocks.control;

import java.lang.reflect.InvocationTargetException;

import org.translator.java.blocks.Block;
import org.translator.java.blocks.BlockConnector;
import org.translator.java.code.CodeSegment;
import org.translator.java.code.IfSegment;
import org.translator.java.code.Value;
import org.w3c.dom.Node;

/**
 *
 * @author Joshua
 */
public class IfBlock extends Block
{
    public IfBlock( Node block )
    {
        super( block );
    }

    public static String getGenusPattern()
    {
        return "if.*";
    }

    public CodeSegment generateCode()
    {
        IfSegment segment = new IfSegment( getTest() );
        CodeSegment[] contained = getContainedCode();

        segment.add( contained[0] );

        if( contained.length > 1 )
            segment.setElse( contained[1] );

        return segment;
    }

    private Value getTest()
    {
        for( BlockConnector connector : connectors )
            if( connector.getLabel().equals( "test" ) && connector.hasConnectedBlock() ) {
            	Block connected = connector.getConnectedBlock();
            	Class<?> c = connected.getClass();
            	try {
            		
            	return (Value) c.getMethod("generateCode").invoke(connected);
            	} catch (NoSuchMethodException e){
            		e.printStackTrace();
            		System.exit(1);
            	} catch (InvocationTargetException e) {
            		e.printStackTrace();
            		System.exit(1);
            	} catch (IllegalAccessException e) {
            		e.printStackTrace();
            		System.exit(1);
            	}
            }

        return new Value( "false" );
    }
}
