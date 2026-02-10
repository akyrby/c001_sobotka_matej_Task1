package controller;

import raster.ZBuffer;
import transforms.*;
import view.Panel;


public class Controller3D {
    private final Panel panel;
    private final ZBuffer zBuffer;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.zBuffer = new ZBuffer(panel.getRaster());

        initListeners();

        drawScene();
    }

    private void initListeners() {
        // TODO: Inicializace listenerů např. pohyb kamerou
    }

    private void drawScene() {
        panel.getRaster().clear();

        zBuffer.setPixelWithZTest(50,50, 0.1,new Col(0x0000ff)); //0.1
        zBuffer.setPixelWithZTest(50,50, 0.5,new Col(0x00ff00)); //0.5

        panel.repaint();
    }
}
