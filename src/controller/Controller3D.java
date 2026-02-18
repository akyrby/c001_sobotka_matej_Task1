package controller;

import model.Vertex;
import raster.ZBuffer;
import rasterize.TriangleRasterizer;
import transforms.*;
import view.Panel;


public class Controller3D {
    private final Panel panel;
    private final ZBuffer zBuffer;
    private final TriangleRasterizer triangleRasterizer;


    public Controller3D(Panel panel) {
        this.panel = panel;
        this.zBuffer = new ZBuffer(panel.getRaster());
        this.triangleRasterizer = new TriangleRasterizer(zBuffer);


        initListeners();

        drawScene();
    }



    private void initListeners() {
        // TODO: Inicializace listenerů např. pohyb kamerou
    }

    private void drawScene() {
        panel.getRaster().clear();

        //zBuffer.setPixelWithZTest(50,50, 0.1,new Col(0x0000ff)); //0.1
        //zBuffer.setPixelWithZTest(50,50, 0.5,new Col(0x00ff00)); //0.5

        triangleRasterizer.rasterize(
                new Vertex(400,0,0.5),
                new Vertex(0,300,0.5),
                new Vertex(799,599,0.5)
        );

        panel.repaint();
    }
}
