package model;

import transforms.Col;
import transforms.Point3D;

public class Vertex implements Vectorizable<Vertex>{
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
    public Vertex(Point3D position, Col color) {
        this.position = position;
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

    @Override
    public Vertex mul(double d) {
        // multiply position and color by scalar and return a new Vertex
        return new Vertex(position.mul(d), col.mul(d));
    }

    @Override
    public Vertex add(Vertex v) {
        // add positions and colors component-wise and return a new Vertex
        return new Vertex(position.add(v.getPosition()), col.add(v.getCol()));
    }
}
