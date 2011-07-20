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

package org.translator.java.blocks.literal;

import org.translator.java.blocks.LiteralBlock;
import org.translator.java.code.CodeSegment;
import org.translator.java.code.Value;

import org.w3c.dom.Node;

/**
 *
 * @author Joshua
 */
public class MathLiteralBlock extends LiteralBlock
{
    public MathLiteralBlock( Node block )
    {
        super( block );
    }

    public static String getGenusPattern()
    {
        return "number";
    }

    public CodeSegment generateCode()
    {
        return new Value( getLabel() );
    }

    public String getDataType()
    {
        return "java.lang.Float";
    }
}
