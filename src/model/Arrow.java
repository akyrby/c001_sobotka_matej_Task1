package model;

import transforms.Col;
import transforms.Mat4Identity;

public class Arrow extends Solid{
    public Arrow(){

        super(new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), new Mat4Identity());


        vertexBuffer.add(new Vertex(200,300,0.5,new Col(200,0,200))); //V0
        vertexBuffer.add(new Vertex(250,300,0.5, new Col(200,200,0))); //V1
        vertexBuffer.add(new Vertex(250,320,0.5, new Col(255,0,0))); //V2
        vertexBuffer.add(new Vertex(270,300,0.5, new Col(0,255,0))); //V3
        vertexBuffer.add(new Vertex(250,280,0.5, new Col(0,0,255))); //V4


        addIndices(0,1); //Lines

        addIndices(4,3,2); //Triangle


        partBuffer.add(new SolidPart(Topology.LINES,0,1));

        partBuffer.add(new SolidPart(Topology.TRIANGLES,2,1));
    }

}
