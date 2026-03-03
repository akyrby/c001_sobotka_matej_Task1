package model;

import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.List;

public class Arrow extends Solid{
    public Arrow(){
        super(List.of(
                new Vertex(0, 0, 0),
                new Vertex(1, 0, 0),
                new Vertex(1, 0.5, 0),
                new Vertex(1.5, 0, 0),
                new Vertex(1, -0.5, 0)
        ), List.of(
                0, 1,
                1, 2,
                1, 3,
                1, 4
        ), List.of(
                new SolidPart(Topology.LINES, 4, 0)
        ), new Mat4Identity());
    }

}
