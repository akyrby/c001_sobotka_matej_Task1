package controller;

import model.Arrow;
import model.Sphere;
import model.Vertex;
import raster.ZBuffer;
import rasterize.LineRasterizerGraphics;
import rasterize.TriangleRasterizer;
import renderer.RendererSolid;
import transforms.Col;
import view.Panel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;


public class Controller3D {
    private final Panel panel;
    private final ZBuffer zBuffer;
    private final TriangleRasterizer triangleRasterizer;

    // line rasterizer for drawing the arrow as lines


    // simple transform parameters for displaying the model
    private double scale = 200.0;
    private int transX;
    private int transY;


    public Controller3D(Panel panel) {
        this.panel = panel;
        this.zBuffer = new ZBuffer(panel.getRaster());
        this.triangleRasterizer = new TriangleRasterizer(zBuffer);



        initListeners();

        drawScene();
    }


    private void initListeners() {

        panel.setFocusable(true);
        panel.requestFocusInWindow();


    }

    private void drawScene() {

        zBuffer.clear();

        panel.getRaster().clear();


        RendererSolid rendererSolid = new RendererSolid(new LineRasterizerGraphics(panel.getRaster()), triangleRasterizer);
        rendererSolid.render(new Arrow());
        rendererSolid.render(new Sphere());

        panel.repaint();
    }
}
