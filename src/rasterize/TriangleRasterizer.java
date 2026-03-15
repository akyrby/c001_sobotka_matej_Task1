package rasterize;

import model.Vertex;
import raster.ZBuffer;
import transforms.Col;

public class TriangleRasterizer extends TriangleRasterizerABS {

    public TriangleRasterizer(ZBuffer zBuffer) {
        super(zBuffer);
    }

    public void rasterize(Vertex a, Vertex b, Vertex c) {

        int ax = (int) Math.round(a.getX());
        int ay = (int) Math.round(a.getY());
        double az = a.getZ();
        Col acol = a.getCol();

        int bx = (int) Math.round(b.getX());
        int by = (int) Math.round(b.getY());
        double bz = b.getZ();
        Col bcol = b.getCol();

        int cx = (int) Math.round(c.getX());
        int cy = (int) Math.round(c.getY());
        double cz = c.getZ();
        Col ccol = c.getCol();


        if (ay > by) {

            int tIX = ax; ax = bx; bx = tIX;
            int tIY = ay; ay = by; by = tIY;
            double tD = az; az = bz; bz = tD;
            Col tC = acol; acol = bcol; bcol = tC;
        }
        if (ay > cy) {

            int tIX = ax; ax = cx; cx = tIX;
            int tIY = ay; ay = cy; cy = tIY;
            double tD = az; az = cz; cz = tD;
            Col tC = acol; acol = ccol; ccol = tC;
        }
        if (by > cy) {

            int tIX = bx; bx = cx; cx = tIX;
            int tIY = by; by = cy; cy = tIY;
            double tD = bz; bz = cz; cz = tD;
            Col tC = bcol; bcol = ccol; ccol = tC;
        }


        if (ay == cy) return;


        if (by != ay) {
            int yFirst = Math.max(ay, 0);
            int yLast  = Math.min(by, zBuffer.getHeight() - 1);
            for (int y = yFirst; y <= yLast; y++) {
                double tAB = (double) (y - ay) / (double) (by - ay);
                double xABd = (1 - tAB) * ax + tAB * bx;
                int xAB = (int) Math.round(xABd);
                double zAB = (1 - tAB) * az + tAB * bz;
                Col colAB = acol.mul(1 - tAB).add(bcol.mul(tAB));

                double tAC = (double) (y - ay) / (double) (cy - ay);
                double xACd = (1 - tAC) * ax + tAC * cx;
                int xAC = (int) Math.round(xACd);
                double zAC = (1 - tAC) * az + tAC * cz;
                Col colAC = acol.mul(1 - tAC).add(ccol.mul(tAC));

                int xStart = Math.min(xAB, xAC);
                int xEnd = Math.max(xAB, xAC);
                double zStart = xAB <= xAC ? zAB : zAC;
                double zEnd = xAB <= xAC ? zAC : zAB;
                Col colStart = xAB <= xAC ? colAB : colAC;
                Col colEnd = xAB <= xAC ? colAC : colAB;

                if (xEnd == xStart) {
                    if (xStart >= 0 && xStart < zBuffer.getWidth())
                        zBuffer.setPixelWithZTest(xStart, y, zStart, colStart);
                } else {
                    int xWriteStart = Math.max(xStart, 0);
                    int xWriteEnd   = Math.min(xEnd,   zBuffer.getWidth() - 1);
                    for (int x = xWriteStart; x <= xWriteEnd; x++) {
                        double t = (x - xStart) / (double) (xEnd - xStart);
                        double z = (1 - t) * zStart + t * zEnd;
                        Col col = colStart.mul(1 - t).add(colEnd.mul(t));
                        zBuffer.setPixelWithZTest(x, y, z, col);
                    }
                }
            }
        }


        if (cy != by) {
            int yFirst = Math.max(by, 0);
            int yLast  = Math.min(cy, zBuffer.getHeight() - 1);
            for (int y = yFirst; y <= yLast; y++) {
                double tBC = (double) (y - by) / (double) (cy - by);
                double xBCd = (1 - tBC) * bx + tBC * cx;
                int xBC = (int) Math.round(xBCd);
                double zBC = (1 - tBC) * bz + tBC * cz;
                Col colBC = bcol.mul(1 - tBC).add(ccol.mul(tBC));

                double tAC = (double) (y - ay) / (double) (cy - ay);
                double xACd = (1 - tAC) * ax + tAC * cx;
                int xAC = (int) Math.round(xACd);
                double zAC = (1 - tAC) * az + tAC * cz;
                Col colAC = acol.mul(1 - tAC).add(ccol.mul(tAC));

                int xStart = Math.min(xBC, xAC);
                int xEnd = Math.max(xBC, xAC);
                double zStart = xBC <= xAC ? zBC : zAC;
                double zEnd = xBC <= xAC ? zAC : zBC;
                Col colStart = xBC <= xAC ? colBC : colAC;
                Col colEnd = xBC <= xAC ? colAC : colBC;

                if (xEnd == xStart) {
                    if (xStart >= 0 && xStart < zBuffer.getWidth())
                        zBuffer.setPixelWithZTest(xStart, y, zStart, colStart);
                } else {
                    int xWriteStart = Math.max(xStart, 0);
                    int xWriteEnd   = Math.min(xEnd,   zBuffer.getWidth() - 1);
                    for (int x = xWriteStart; x <= xWriteEnd; x++) {
                        double t = (x - xStart) / (double) (xEnd - xStart);
                        double z = (1 - t) * zStart + t * zEnd;
                        Col col = colStart.mul(1 - t).add(colEnd.mul(t));
                        zBuffer.setPixelWithZTest(x, y, z, col);
                    }
                }
            }
        }
    }
}
