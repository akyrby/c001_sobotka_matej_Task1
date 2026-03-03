package model;

import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.List;

public class Sphere extends Solid{
//    public Spehre(){
//        super(List.of()
//                new Vertex(0, 0, 0)
//        ), List.of(
//                0
//        ), List.of(
//                new SolidPart(Topology.POINTS, 1, 0)
//        ), new Mat4Identity());
//    }

    public Sphere(List<Vertex> vertexBuffer, List<Integer> indexBuffer, List<SolidPart> partBuffer, Mat4 modelMat) {
        super(vertexBuffer, indexBuffer, partBuffer, modelMat);
    }
}
