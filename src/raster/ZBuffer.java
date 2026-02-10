package raster;

import transforms.Col;

public class ZBuffer {
    private final Raster<Col> imageBuffer;
    private final Raster<Double> depthBuffer;

    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBufer(imageBuffer.getWidth(), imageBuffer.getHeight());
        this.depthBuffer.clear();
    }
    
    public void clear() {
        imageBuffer.clear();
        depthBuffer.clear();
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        if (x < 0 || x >= imageBuffer.getWidth() || y < 0 || y >= imageBuffer.getHeight()) {
            return;
        }

        // TODO: načtu hodnotu z depth bufferu
        // TODO: porovnám hodnotu s hodnotou Z, která přišla do metody
        // TODO: podle podmínky
            // TODO: 1. nedělám nic
            // TODO: 2. obarvím pixel, updatuju depth buffer
        double depthValue = depthBuffer.getValue(x, y).orElse(Double.POSITIVE_INFINITY);
        
        if (z < depthValue) {
            imageBuffer.setValue(x, y, color);
            depthBuffer.setValue(x, y, z);
        }
    }
}
