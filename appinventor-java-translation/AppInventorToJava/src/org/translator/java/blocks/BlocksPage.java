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

package org.translator.java.blocks;

import org.translator.java.blocks.definition.DefinitionBlock;
import org.translator.java.blocks.definition.EventDefinitionBlock;
import org.translator.java.code.CodeSegment;
import org.translator.java.code.FunctionCall;
import org.translator.java.code.IfSegment;
import org.translator.java.code.Value;
import org.translator.java.code.ValueStatement;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Joshua
 */
public class BlocksPage
{
    private final ArrayList<DefinitionBlock> blocks = new ArrayList<DefinitionBlock>();
    private BlocksFactory loader = new BlocksFactory();

    public BlocksPage( Node page )
    {
        NodeList children = page.getChildNodes();

        for( int i = 0; i < children.getLength(); i++ )
            if( children.item( i ).getNodeName().equals( "PageBlocks" ))
            {
                blocks.addAll( loader.loadBlocks( children.item( i ).getChildNodes() ));
                break;
            }
    }

    public CodeSegment declaration()
    {
        CodeSegment code = new CodeSegment();

        for( DefinitionBlock block : blocks )
            code.add( block.declare() );

        return code;
    }

    public CodeSegment definition()
    {
        CodeSegment code = new CodeSegment();

        for( DefinitionBlock block : blocks )
            code.add( block.define() );

        return code;
    }

    public CodeSegment toCode()
    {
        CodeSegment codeBlock = new CodeSegment();

        for( Block b : blocks )
            codeBlock.add( b.toCode() );

        return codeBlock;
    }

    public boolean hasEvents()
    {
        for( Block b : blocks )
            if( b instanceof EventDefinitionBlock )
                return true;

        return false;
    }

    public SortedSet<String> getEvents()
    {
        SortedSet<String> eventSet = new TreeSet<String>();

        for( Block b : blocks )
            eventSet.addAll( b.getEvents() );

        return eventSet;
    }

    public CodeSegment createDispatchSegment( String className )
    {
        CodeSegment segment = new CodeSegment();
        IfSegment ifThen = null;

        for( Block b : blocks )
        {
            for( String event : b.getEvents() )
            {
                String component = getNameFromLabel( b.getLabel() );
                if( component.equals( className ))
                    component = "this";
                Value v = new Value( String.format( "component.equals( %s ) && eventName.equals( \"%s\" )", component, event ));
                IfSegment newIf = new IfSegment( v );
                newIf.add( new ValueStatement( getEventCall( (EventDefinitionBlock)b )));
                if( ifThen == null )
                {
                    ifThen = newIf;
                    segment.add( ifThen );
                } else {
                    segment.add( newIf );
                }
            }
        }

        return segment;
    }

    private String getNameFromLabel( String label )
    {
        return label.substring( 0, label.indexOf( "." ));
    }

    private FunctionCall getEventCall( EventDefinitionBlock b )
    {
        ArrayList<Value> params = new ArrayList<Value>();

        for( int i : b.getParameterNumbers() )
            params.add( new Value( String.format( "args[%d]", i )));

        return new FunctionCall( b.getFunctionName(), params );
    }
}
