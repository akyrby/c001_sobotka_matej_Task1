package model;

import transforms.Col;
import transforms.Point3D;

public class Vertex {
    private final Point3D position;
    private final Col col;

    public Vertex(double x, double y, double z) {
        this.position = new Point3D(x, y, z);
        this.col = new Col(0xFFFFFF);
    }

    public Vertex(double x, double y, double z, Col color) {
        this.position = new Point3D(x, y, z);
        this.col = color;
    }

    public Point3D getPosition() {
        return position;
    }
    public double getX(){
        return position.getX();
    }
    public double getY(){
        return position.getY();
    }
    public double getZ() {
        return position.getZ();
    }
    public Col getCol() {
        return col;
    }

}
