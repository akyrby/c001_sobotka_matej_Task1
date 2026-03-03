package model;

import transforms.Mat4;

import java.util.List;

public abstract class Solid {
    private final List<Vertex> vertexBuffer;
    private final List<Integer> indexBuffer;
    private final List<SolidPart> partBuffer;

    private final Mat4 modelMat;

    public Solid(final List<Vertex> vertexBuffer,final List<Integer> indexBuffer,List<SolidPart> partBuffer,final Mat4 modelMat) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.modelMat = modelMat;
        this.partBuffer = partBuffer;
    }

    // Public getters so rendering code can access geometry
    public List<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public List<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public List<SolidPart> getPartBuffer() {
        return partBuffer;
    }

    public Mat4 getModelMat() {
        return modelMat;
    }
}
