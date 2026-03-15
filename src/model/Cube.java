package model;

public class Cube extends Solid {
    public Cube() {
        super(new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), new transforms.Mat4Identity());
        // Define the 8 vertices of the cube
        vertexBuffer.add(new Vertex(100, 100, 0.5, new transforms.Col(255, 0, 0))); // V0
        vertexBuffer.add(new Vertex(200, 100, 0.5, new transforms.Col(0, 255, 0))); // V1
        vertexBuffer.add(new Vertex(200, 200, 0.5, new transforms.Col(0, 0, 255))); // V2
        vertexBuffer.add(new Vertex(100, 200, 0.5, new transforms.Col(255, 255, 0))); // V3
        vertexBuffer.add(new Vertex(100, 100, 0.3, new transforms.Col(255, 0, 255))); // V4
        vertexBuffer.add(new Vertex(200, 100, 0.3, new transforms.Col(0, 255, 255))); // V5
        vertexBuffer.add(new Vertex(200, 200, 0.3, new transforms.Col(255, 255, 255))); // V6
        vertexBuffer.add(new Vertex(100, 200, 0.3, new transforms.Col(128, 128, 128))); // V7

        // Define the 12 triangles (two per face)
        addIndices(0, 1, 2); // Front face
        addIndices(0, 2, 3);
        addIndices(4, 5, 6); // Back face
        addIndices(4, 6, 7);
        addIndices(0, 1, 5); // Top face
        addIndices(0, 5, 4);
        addIndices(2, 3, 7); // Bottom face
        addIndices(2, 7, 6);
        addIndices(1, 2, 6); // Right face
        addIndices(1, 6, 5);
        addIndices(0, 3, 7); // Left face
        addIndices(0, 7, 4);

        // Define the parts (one part for the whole cube)
        partBuffer.add(new SolidPart(Topology.TRIANGLES, 12, 0)); // 12 triangles = 36 indices
        
    }

}
