package renderer;

import model.Solid;
import model.SolidPart;
import model.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;

public class RendererSolid {
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;


    public RendererSolid(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
    }

    public void render(Solid solid){
        for(SolidPart part : solid.getPartBuffer()){
            switch (part.getTopology()){
                case POINTS:
                    //TODO: points
                    break;
                case LINES:
                    //TODO: lines
                    int index = part.getStartIndex();
                    for(int i =0; i < part.getPrimitiveCount(); i++){
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA);
                        Vertex b = solid.getVertexBuffer().get(indexB);
                        //TODO: Vrcholy pronásobím MVP maticí

                        //TODO: Ořežání

                        //TODO:Dehomogenizace

                        //TODO: Transformace do okna

                        //TODO: Rasterizace
                        lineRasterizer.rasterize(
                                (int) Math.round(a.getX()),
                                (int) Math.round(a.getY()),
                                (int) Math.round(b.getX()),
                                (int) Math.round(b.getY())
                        );
                    }
                    break;
                case TRIANGLES:
                    //TODO: triangles
                        index = part.getStartIndex();
                    for(int i =0; i < part.getPrimitiveCount(); i++){
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA);
                        Vertex b = solid.getVertexBuffer().get(indexB);
                        Vertex c = solid.getVertexBuffer().get(indexC);
                        //TODO: Vrcholy pronásobím MVP maticí


                        //TODO: Ořežání

                        //TODO:Dehomogenizace

                        //TODO: Transformace do okna

                        //TODO: Rasterizace
                        triangleRasterizer.rasterize(a, b, c);
                    }
                    break;
            }
        }
    }
}
