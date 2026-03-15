package model;

import transforms.Col;
import transforms.Mat4Identity;

public class Cylinder extends Solid {
    public Cylinder() {
        super(new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), new Mat4Identity());

        int segments = 20;
        double radius = 50;
        double cx = 200;
        double cy = 200;
        double zBottom = 0.5;
        double zTop = 0.3;

        // Ring vertices: even indices = bottom, odd indices = top
        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            double x = cx + radius * Math.cos(angle);
            double y = cy + radius * Math.sin(angle);
            vertexBuffer.add(new Vertex(x, y, zBottom, new Col(255, 0, 0)));
            vertexBuffer.add(new Vertex(x, y, zTop, new Col(0, 255, 0)));
        }

        int topCenter = vertexBuffer.size();
        vertexBuffer.add(new Vertex(cx, cy, zTop, new Col(0, 200, 0)));
        int bottomCenter = vertexBuffer.size();
        vertexBuffer.add(new Vertex(cx, cy, zBottom, new Col(200, 0, 0)));

        int triangleCount = 0;
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;

            int b0 = 2 * i;
            int t0 = b0 + 1;
            int b1 = 2 * next;
            int t1 = b1 + 1;

            // Side quad split into two triangles
            addIndices(b0, b1, t1);
            addIndices(b0, t1, t0);
            triangleCount += 2;

            // Top cap
            addIndices(t0, t1, topCenter);
            triangleCount++;

            // Bottom cap
            addIndices(b0, bottomCenter, b1);
            triangleCount++;
        }

        partBuffer.add(new SolidPart(Topology.TRIANGLES, triangleCount, 0));
    }

}
