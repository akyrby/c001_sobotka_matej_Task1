package rasterize;

import model.Vertex;
import raster.ZBuffer;
import transforms.Col;

public class TriangleRasterizer {
    private final ZBuffer zBuffer;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(Vertex a, Vertex b, Vertex c) {
        //TODO: Ay <= By <= Cy prohazovat všechny souřadnice bodů -> Seřadit
        int ax = (int)Math.round(a.getX());
        int ay = (int)Math.round(a.getY());
        double az = a.getZ();

        int bx = (int)Math.round(b.getX());
        int by = (int)Math.round(b.getY());
        double bz = b.getZ();

        int cx = (int)Math.round(c.getX());
        int cy = (int)Math.round(c.getY());
        double cz = c.getZ();

        //1.Část trojúhelníku
        for(int y = ay; y <= by; y++) {
            // Hrana AB
            //TODO: Spočítat interpolační koeficient
            // tAB - interpolační koeficient úsečky AB
            // double tAB =
            // int xAB =
            double tAB = (double) (y - ay) /(bx-ay);
            int xAB = (int) Math.round((1-tAB)*ax+tAB*bx);
            //TODO: Spočítat zAB




            //Hrana AC
            // double tAC =
            // int xAC =
            //TODO: Spočítat zAC
            double tAC = (double) (y - ay) /(cy-ay);
            int xAC = (int) Math.round((1-tAC)*ax+tAC*cx);

            //TODO: Kontrola, že xAB < xAC - pokud ne = prohodit


            //TODO: Napsat cyklus od xAB do xAC a obarvit pixely
            for(int x = xAB; x <= xAC; x++) {
                //TODO: Interpolační koeficient hrana
                double t = (x-xAB)/(double)(xAC-xAB);
                //TODO: Spočítat finalní Z
                //Teoretický vzoreček, dá se použít   int z = (1-t) * zAB + t * zAC
                zBuffer.setPixelWithZTest(x,y,0.5,new Col(0xffffff));
            }
        }
        // TODO: Dodělat 2. část trojnelníku

        for(int y = by; y <= cy; y++){
            //tBC =
            // int xBC =
            double tBC = (double) (y - ay) /(bx-by);
            int xBC = (int)Math.round((1-tBC)*ay+tBC*cx);

            //Hrana AC
            // double tAC =
            // int xAC =
            double tAC = (double) (y - ay) /(cy-ay);
            int xAC = (int) Math.round((1-tAC)*ax+tAC*cx);
        }
    }
}
