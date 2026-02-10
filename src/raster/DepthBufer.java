package raster;

import java.util.Optional;

public class DepthBufer implements Raster<Double>{
    private final double [][] buffer;
    private final int width;
    private final int height;

    public DepthBufer(int width, int height) {
        this.buffer = new double[height][width];
        this.width = width;
        this.height = height;
    }

    @Override
    public void setValue(int x, int y, Double value) {
        //TODO: implementovat
    }

    @Override
    public Optional<Double> getValue(int x, int y) {
        //TODO: implementovat
        return Optional.empty();
    }

    @Override
    public int getWidth() {
        //TODO: implementovat
        return width;
    }

    @Override
    public int getHeight() {
        //TODO: implementovat
        return height;
    }

    @Override
    public void clear() {

        //TODO: implementovat
    }
}
