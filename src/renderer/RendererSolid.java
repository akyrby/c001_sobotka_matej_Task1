package renderer;

import model.Solid;
import model.SolidPart;
import model.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;
import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RendererSolid {
    private final LineRasterizer lineRasterizer;
    private final TriangleRasterizer triangleRasterizer;
    private final int viewportWidth;
    private final int viewportHeight;

    private Mat4 viewMatrix = new Mat4Identity();
    private Mat4 projectionMatrix = new Mat4Identity();
    private boolean wireframe = false;

    public RendererSolid(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer,
                         int viewportWidth, int viewportHeight) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public RendererSolid(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer) {
        this(lineRasterizer, triangleRasterizer, 800, 600);
    }

    public void setViewMatrix(Mat4 viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void setProjectionMatrix(Mat4 projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public void render(Solid solid) {
        render(solid, solid.getModelMat());
    }

    public void render(Solid solid, Mat4 modelMatrix) {
        Mat4 mvp = modelMatrix.mul(viewMatrix).mul(projectionMatrix);

        for (SolidPart part : solid.getPartBuffer()) {
            switch (part.getTopology()) {
                case POINTS:
                    break;
                case LINE_LIST:
                case LINE_STRIP:
                case LINES:
                    int index = part.getStartIndex();
                    for (int i = 0; i < part.getPrimitiveCount(); i++) {
                        Vertex a = solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++));
                        Vertex b = solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++));
                        renderLine(a, b, mvp);
                    }
                    break;
                case TRIANGLE_LIST:
                case TRIANGLE_STRIP:
                case TRIANGLE_FAN:
                case TRIANGLES:
                    index = part.getStartIndex();
                    for (int i = 0; i < part.getPrimitiveCount(); i++) {
                        Vertex a = solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++));
                        Vertex b = solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++));
                        Vertex c = solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++));
                        renderTriangle(a, b, c, mvp);
                    }
                    break;
            }
        }
    }

    //  Lines 

    private void renderLine(Vertex vA, Vertex vB, Mat4 mvp) {
        Point3D cA = vA.getPosition().mul(mvp);
        Point3D cB = vB.getPosition().mul(mvp);

        boolean aIn = cA.getZ() >= 0.0 && cA.getW() > 0.0;
        boolean bIn = cB.getZ() >= 0.0 && cB.getW() > 0.0;

        if (!aIn && !bIn) return;

        Col colA = vA.getCol();
        Col colB = vB.getCol();

        if (!aIn) {
            double t = cA.getZ() / (cA.getZ() - cB.getZ());
            cA = lerpPt(cA, cB, t);
            colA = lerpCol(colA, colB, t);
        } else if (!bIn) {
            double t = cA.getZ() / (cA.getZ() - cB.getZ());
            cB = lerpPt(cA, cB, t);
            colB = lerpCol(colA, colB, t);
        }

        Vertex sA = toScreen(cA, colA);
        Vertex sB = toScreen(cB, colB);
        if (sA == null || sB == null) return;

        lineRasterizer.rasterize(
                (int) Math.round(sA.getX()), (int) Math.round(sA.getY()),
                (int) Math.round(sB.getX()), (int) Math.round(sB.getY()));
    }

    //  Triangles 

    private void renderTriangle(Vertex vA, Vertex vB, Vertex vC, Mat4 mvp) {
        Point3D cA = vA.getPosition().mul(mvp);
        Point3D cB = vB.getPosition().mul(mvp);
        Point3D cC = vC.getPosition().mul(mvp);

        clipAndRenderTriangle(
                new Point3D[]{cA, cB, cC},
                new Col[]{vA.getCol(), vB.getCol(), vC.getCol()});
    }

    /**
     * Sutherland-Hodgman clip against the near plane (z_clip >= 0),
     * then transform each resulting triangle to screen space and rasterize.
     */
    private void clipAndRenderTriangle(Point3D[] clipPts, Col[] cols) {
        List<Point3D> outPts = new ArrayList<>(6);
        List<Col> outCols = new ArrayList<>(6);

        int n = clipPts.length;
        for (int i = 0; i < n; i++) {
            Point3D curr = clipPts[i];
            Point3D next = clipPts[(i + 1) % n];
            Col ccurr = cols[i];
            Col cnext = cols[(i + 1) % n];

            boolean currIn = curr.getZ() >= 0.0;
            boolean nextIn = next.getZ() >= 0.0;

            if (currIn) {
                outPts.add(curr);
                outCols.add(ccurr);
            }
            if (currIn != nextIn) {
                double t = curr.getZ() / (curr.getZ() - next.getZ());
                outPts.add(lerpPt(curr, next, t));
                outCols.add(lerpCol(ccurr, cnext, t));
            }
        }

        for (int i = 1; i + 1 < outPts.size(); i++) {
            Vertex sA = toScreen(outPts.get(0), outCols.get(0));
            Vertex sB = toScreen(outPts.get(i), outCols.get(i));
            Vertex sC = toScreen(outPts.get(i + 1), outCols.get(i + 1));
            if (sA != null && sB != null && sC != null) {
                if (wireframe) {
                    rasterizeWireEdge(sA, sB);
                    rasterizeWireEdge(sB, sC);
                    rasterizeWireEdge(sC, sA);
                } else {
                    triangleRasterizer.rasterize(sA, sB, sC);
                }
            }
        }
    }

    //  Helpers 

    private void rasterizeWireEdge(Vertex a, Vertex b) {
        lineRasterizer.rasterize(
                (int) Math.round(a.getX()), (int) Math.round(a.getY()),
                (int) Math.round(b.getX()), (int) Math.round(b.getY()));
    }

    /** Dehomogenize clip point and map to viewport. Returns null if behind camera or outside z [0,1]. */
    private Vertex toScreen(Point3D clipPt, Col col) {
        if (clipPt.getW() <= 0.0) return null;
        Optional<Vec3D> ndcOpt = clipPt.dehomog();
        if (!ndcOpt.isPresent()) return null;
        Vec3D ndc = ndcOpt.get();
        if (ndc.getZ() > 1.0) return null;
        double x = (ndc.getX() + 1.0) * 0.5 * (viewportWidth - 1);
        double y = (1.0 - ndc.getY()) * 0.5 * (viewportHeight - 1);
        return new Vertex(x, y, ndc.getZ(), col);
    }

    private static Point3D lerpPt(Point3D a, Point3D b, double t) {
        return new Point3D(
                a.getX() + t * (b.getX() - a.getX()),
                a.getY() + t * (b.getY() - a.getY()),
                a.getZ() + t * (b.getZ() - a.getZ()),
                a.getW() + t * (b.getW() - a.getW()));
    }

    private static Col lerpCol(Col a, Col b, double t) {
        return a.mul(1.0 - t).add(b.mul(t));
    }
}