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

package org.translator.java;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.translator.java.blocks.BlocksPage;
import org.translator.java.code.ClassSegment;
import org.translator.java.code.SourceFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

/**
 *
 * @author jswank
 */
public class AppInventorScreen
{
    private String projectName;
    private final HashMap<String, String> data = new HashMap<String, String>();
    private final HashMap<String, String> types = new HashMap<String, String>();
    private final ArrayList<BlocksPage> blocksPages = new ArrayList<BlocksPage>();
    private AppInventorProperties form;

    protected AppInventorScreen( String projectName ) throws IOException
    {
        this.projectName = projectName;
    }

    public String getName()
    {
        return form.getName();
    }
    
    public Map<String, String> getTypes() {
    	return types;
    }
    
    protected void loadBlocksFile( InputStream inputStream ) throws IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setValidating( false );

        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Ignores dtd files
            builder.setEntityResolver( new EntityResolver() {
                public InputSource resolveEntity( String publicId, String systemId ) throws SAXException, IOException
                {
                    return new InputSource( new ByteArrayInputStream("".getBytes()));
                }
            });

            Scanner s = new Scanner( inputStream );
            s.useDelimiter( "\\Z" );

            Document blocksDoc = builder.parse( new ByteArrayInputStream( s.next().getBytes() ));
            Element e = blocksDoc.getDocumentElement();

            NodeList nl = e.getChildNodes();

            for( int i = 0; i < nl.getLength(); i++ ) {
                if( nl.item( i ).getNodeName().equals( "Pages" ))
                {
                    loadPages( nl.item( i ));
                } else if(nl.item(i).getNodeName().equals("YoungAndroidMaps")) {
                	loadMaps(nl.item(i));
                }
            }
        } catch( Exception e ) {
        	e.printStackTrace();
        }
    }

    private void loadPages( Node pagesNode )
    {
        NodeList pages = pagesNode.getChildNodes();

        for( int i = 0; i < pages.getLength(); i++ )
            if( pages.item( i ).getNodeName().equals( "Page" ))
                blocksPages.add( new BlocksPage( pages.item( i )));
    }
    
    private void loadMaps(Node mapsNode) {
    	NodeList maps = mapsNode.getChildNodes();
    	for (int i = 0; i < maps.getLength(); i++) {
    		if (maps.item(i).getNodeName().equals("YoungAndroidUuidMap"))
    			loadUuidMap(maps.item(i));
    	}
    }
    
    private void loadUuidMap(Node uuidMap) {
    	NodeList entries = uuidMap.getChildNodes();
    	for (int i = 0; i < entries.getLength(); i++) {
    		Node entry = entries.item(i);
    		if (entry.hasAttributes()) {
	    		String genus = entry.getAttributes().getNamedItem("component-genus").getNodeValue();
	    		String objectName = entry.getAttributes().getNamedItem("component-id").getNodeValue();
	    		types.put(objectName, genus);
    		}
    	}
    }
    
    protected void loadComponentFile( InputStream inputStream ) throws IOException
    {
        BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ));

        reader.skip( 9 );
        parseComponentJSON( new JsonReader( reader ));
    }

    protected SourceFile generateJavaFile()
    {
        SourceFile file = new SourceFile( TranslatorConstants.PACKAGE_PREFIX.concat( projectName.toLowerCase() ));

        if( form != null )
            file.setMainClass( (ClassSegment)form.generateCode( blocksPages, projectName ));

        return file;
    }
    
    protected void loadInterfaceFile( InputStream inputStream ) throws IOException
    {
        //TODO: Implement
    }

    private void parseComponentJSON( JsonReader reader ) throws IOException
    {
        reader.beginObject();

        while( reader.peek() == JsonToken.NAME )
        {
            String name = reader.nextName();
            if( name.equals( "Properties" ) )
                form = new AppInventorProperties( reader );
            else {
            	String d = reader.nextString();
            	if (d.matches("True|False")) d = d.toLowerCase();
                data.put( name, d );
            }
        }

        reader.endObject();
    }
}
