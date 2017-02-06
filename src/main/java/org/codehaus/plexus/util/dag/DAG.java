package org.codehaus.plexus.util.dag;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAG = Directed Acyclic Graph
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 * @todo this class should be renamed from DAG to Dag
 */
public class DAG implements Cloneable, Serializable
{
    //------------------------------------------------------------
    //Fields
    //------------------------------------------------------------
    /**
     * Nodes will be kept in two data structures at the same time
     * for faster processing
     */
    /**
     * Maps vertex's label to vertex
     */
    private Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();

    /**
     * Contain list of all verticies
     */
    private List<Vertex> vertexList = new ArrayList<Vertex>();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     *
     */
    public DAG()
    {
        super();
    }

    // ------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------

    /**
     * @return
     */
    public List<Vertex> getVerticies()
    {
        return vertexList;
    }


    public Set<String> getLabels()
    {
        return vertexMap.keySet();
    }

    // ------------------------------------------------------------
    // Implementation
    // ------------------------------------------------------------

    /**
     * Adds vertex to DAG. If vertex of given label already exist in DAG
     * no vertex is added
     *
     * @param label The label of the Vertex
     * @return New vertex if vertex of given label was not present in the DAG
     *         or existing vertex if vertex of given label was already added to DAG
     */
    public Vertex addVertex( final String label )
    {
        Vertex retValue = null;

        // check if vertex is already in DAG
        if ( vertexMap.containsKey( label ) )
        {
            retValue = vertexMap.get( label );
        }
        else
        {
            retValue = new Vertex( label );

            vertexMap.put( label, retValue );

            vertexList.add( retValue );
        }

        return retValue;
    }

    public void addEdge( final String from, final String to ) throws CycleDetectedException
    {
        final Vertex v1 = addVertex( from );

        final Vertex v2 = addVertex( to );

        addEdge( v1, v2 );
    }

    public void addEdge( final Vertex from, final Vertex to ) throws CycleDetectedException
    {

        from.addEdgeTo( to );

        to.addEdgeFrom( from );

        final List<String> cycle = CycleDetector.introducesCycle( to );

        if ( cycle != null )
        {
            // remove edge which introduced cycle

            removeEdge( from, to );

            final String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";

            throw new CycleDetectedException( msg, cycle );
        }
    }


    public void removeEdge( final String from, final String to )
    {
        final Vertex v1 = addVertex( from );

        final Vertex v2 = addVertex( to );

        removeEdge( v1, v2 );
    }

    public void removeEdge( final Vertex from, final Vertex to )
    {
        from.removeEdgeTo( to );

        to.removeEdgeFrom( from );
    }


    public Vertex getVertex( final String label )
    {
        final Vertex retValue = ( Vertex ) vertexMap.get( label );

        return retValue;
    }

    public boolean hasEdge( final String label1, final String label2 )
    {
        final Vertex v1 = getVertex( label1 );

        final Vertex v2 = getVertex( label2 );

        final boolean retValue = v1.getChildren().contains( v2 );

        return retValue;

    }

    /**
     * @param label
     * @return
     */
    public List<String> getChildLabels( final String label )
    {
        final Vertex vertex = getVertex( label );

        return vertex.getChildLabels();
    }

    /**
     * @param label
     * @return
     */
    public List<String> getParentLabels( final String label )
    {
        final Vertex vertex = getVertex( label );

        return vertex.getParentLabels();
    }


    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() throws CloneNotSupportedException
    {
        // this is what's failing..
        final Object retValue = super.clone();

        return retValue;
    }


    /**
     * Indicates if there is at least one edge leading to or from vertex of given label
     *
     * @return <code>true</true> if this vertex is connected with other vertex,<code>false</code> otherwise
     */
    public boolean isConnected( final String label )
    {
        final Vertex vertex = getVertex( label );

        final boolean retValue = vertex.isConnected();

        return retValue;

    }


    /**
     * Return the list of labels of successor in order decided by topological sort
     *
     * @param label The label of the vertex whose predecessors are searched
     *
     * @return The list of labels. Returned list contains also
     *         the label passed as parameter to this method. This label should
     *         always be the last item in the list.
     */
    public List<String> getSuccessorLabels( final String label )
    {
        final Vertex vertex = getVertex( label );

        final List<String> retValue;

        //optimization.
        if ( vertex.isLeaf() )
        {
            retValue = new ArrayList<String>( 1 );

            retValue.add( label );
        }
        else
        {
            retValue = TopologicalSorter.sort( vertex );
        }

        return retValue;
    }


}
