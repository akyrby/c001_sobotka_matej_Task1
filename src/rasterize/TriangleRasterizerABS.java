package rasterize;

import model.Vertex;
import raster.ZBuffer;

/**
 * Abstract base class for triangle rasterizers.
 * Concrete rasterizers should extend this and implement the rasterize method.
 */
public abstract class TriangleRasterizerABS {
    protected final ZBuffer zBuffer;

    /**
     * Create a rasterizer that writes into the provided Z-buffer.
     * @param zBuffer the Z-buffer to use for drawing and depth tests
     */
    public TriangleRasterizerABS(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    /**
     * Rasterize a triangle defined by three vertices (screen coordinates + depth).
     * Implementations must use the provided Z-buffer for depth testing.
     * @param a first vertex
     * @param b second vertex
     * @param c third vertex
     */
    public abstract void rasterize(Vertex a, Vertex b, Vertex c);

}
