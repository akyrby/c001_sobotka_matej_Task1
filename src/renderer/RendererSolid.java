package renderer;

import model.Solid;
import model.SolidPart;
import model.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.Optional;

public class RendererSolid {
    private final LineRasterizer lineRasterizer;
    private final TriangleRasterizer triangleRasterizer;
    private final int viewportWidth;
    private final int viewportHeight;

    private Mat4 viewMatrix = new Mat4Identity();
    private Mat4 projectionMatrix = new Mat4Identity();


    public RendererSolid(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer, int viewportWidth, int viewportHeight) {
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

    public void render(Solid solid) {
        render(solid, solid.getModelMat());
    }

    public void render(Solid solid, Mat4 modelMatrix){
        Mat4 mvp = modelMatrix.mul(viewMatrix).mul(projectionMatrix);

        for(SolidPart part : solid.getPartBuffer()){
            switch (part.getTopology()){
                case POINTS:
                    //TODO: points
                    break;
                case LINE_LIST:
                case LINE_STRIP:
                case LINES:
                    int index = part.getStartIndex();
                    for(int i =0; i < part.getPrimitiveCount(); i++){
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA);
                        Vertex b = solid.getVertexBuffer().get(indexB);

                        Vertex aT = transformVertex(a, mvp);
                        Vertex bT = transformVertex(b, mvp);
                        if (aT == null || bT == null) {
                            continue;
                        }

                        lineRasterizer.rasterize(
                                (int) Math.round(aT.getX()),
                                (int) Math.round(aT.getY()),
                                (int) Math.round(bT.getX()),
                                (int) Math.round(bT.getY())
                        );
                    }
                    break;
                case TRIANGLE_LIST:
                case TRIANGLE_STRIP:
                case TRIANGLE_FAN:
                case TRIANGLES:
                        index = part.getStartIndex();
                    for(int i =0; i < part.getPrimitiveCount(); i++){
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA);
                        Vertex b = solid.getVertexBuffer().get(indexB);
                        Vertex c = solid.getVertexBuffer().get(indexC);

                        Vertex aT = transformVertex(a, mvp);
                        Vertex bT = transformVertex(b, mvp);
                        Vertex cT = transformVertex(c, mvp);
                        if (aT == null || bT == null || cT == null) {
                            continue;
                        }

                        triangleRasterizer.rasterize(aT, bT, cT);
                    }
                    break;
            }
        }
    }

    private Vertex transformVertex(Vertex source, Mat4 mvp) {
        Point3D clipPoint = source.getPosition().mul(mvp);
        if (clipPoint.getW() <= 0.0) {
            return null;
        }

        Optional<Vec3D> ndcOptional = clipPoint.dehomog();
        if (!ndcOptional.isPresent()) {
            return null;
        }

        Vec3D ndc = ndcOptional.get();
        if (ndc.getX() < -1.0 || ndc.getX() > 1.0
                || ndc.getY() < -1.0 || ndc.getY() > 1.0
                || ndc.getZ() < 0.0 || ndc.getZ() > 1.0) {
            return null;
        }

        double x = (ndc.getX() + 1.0) * 0.5 * (viewportWidth - 1);
        double y = (1.0 - ndc.getY()) * 0.5 * (viewportHeight - 1);

        return new Vertex(x, y, ndc.getZ(), source.getCol());
    }
}
