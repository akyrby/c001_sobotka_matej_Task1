package model;

import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.ArrayList;
import java.util.List;

public class Sphere extends Solid {

    /**
     * Builds a UV sphere centred at (cx, cy) with the given radius.
     *
     * @param cx      screen-space centre X
     * @param cy      screen-space centre Y
     * @param radius  sphere radius in pixels
     * @param stacks  number of horizontal rings  (latitude subdivisions)
     * @param sectors number of vertical slices   (longitude subdivisions)
     */
    public Sphere(double cx, double cy, double radius, int stacks, int sectors) {
        super(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new Mat4Identity());

        // ── Vertices ────────────────────────────────────────────────────────────
        // phi   : elevation from +Y pole down to -Y pole  ( PI/2  →  -PI/2 )
        // theta : azimuth around the Y axis               ( 0  →  2*PI )
        for (int i = 0; i <= stacks; i++) {
            double phi = Math.PI / 2.0 - i * Math.PI / stacks;
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);

            for (int j = 0; j <= sectors; j++) {
                double theta = j * 2.0 * Math.PI / sectors;
                double cosTheta = Math.cos(theta);
                double sinTheta = Math.sin(theta);

                // screen-space position (Y axis points downward on screen)
                double x = cx + radius * cosPhi * cosTheta;
                double y = cy - radius * sinPhi;        // inverted Y for screen
                double z = 0.5 - 0.49 * cosPhi * sinTheta; // depth: front→0, back→1

                // colour varies smoothly across the sphere surface
                int r = (int) (255 * (0.5 + 0.5 * cosTheta));
                int g = (int) (255 * (0.5 + 0.5 * sinPhi));
                int b = (int) (255 * (0.5 + 0.5 * sinTheta));

                vertexBuffer.add(new Vertex(x, y, z, new Col(r, g, b)));
            }
        }

        // ── Indices (two triangles per quad) ────────────────────────────────────
        int triCount = 0;
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < sectors; j++) {
                int v1 = i * (sectors + 1) + j;
                int v2 = v1 + 1;
                int v3 = v1 + (sectors + 1);
                int v4 = v3 + 1;

                // skip degenerate triangles at the poles
                if (i != 0) {
                    addIndices(v1, v2, v3);
                    triCount++;
                }
                if (i != stacks - 1) {
                    addIndices(v2, v4, v3);
                    triCount++;
                }
            }
        }

        partBuffer.add(new SolidPart(Topology.TRIANGLES, triCount, 0));
    }

    /** Convenience constructor – sphere at (400, 300) with radius 100 using 24 stacks/sectors. */
    public Sphere() {
        this(400, 300, 100, 24, 24);
    }

    /** Pass-through constructor for external vertex/index/part buffers. */
    public Sphere(List<Vertex> vertexBuffer, List<Integer> indexBuffer,
                  List<SolidPart> partBuffer, Mat4 modelMat) {
        super(vertexBuffer, indexBuffer, partBuffer, modelMat);
    }
}
