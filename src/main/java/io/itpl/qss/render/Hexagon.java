package io.itpl.qss.render;

import java.awt.*;

public class Hexagon {
    private int radius;
    private Point center;
    private Polygon hexagon;
    private String info;
    
    public Hexagon (int x, int y, int width){
        this.radius = width/2;
        center = new Point(x+radius, y+radius);
        this.info = "Polygon: radius[" +this.radius +"] x[" +center.x + "] y["+center.y+"]";  
        this.hexagon = createHexagon();
    }
    public Hexagon (int x, int y, int width,String info){
        this.radius = width/2;
        center = new Point(x+radius, y+radius);
        this.info = this.info + "{ Polygon: radius[" +this.radius +"] x[" +center.x + "] y["+center.y+"] }";  
        this.hexagon = createHexagon();
    }
    public Hexagon(Point center, int radius) {
        this.center = center;
        this.radius = radius;
        this.hexagon = createHexagon();
    }

    private Polygon createHexagon() {
        Polygon polygon = new Polygon();
        StringBuffer msg = new StringBuffer("\nPoints:\n");
        for (int i = 0; i < 6; i++) {
            int xval = (int) (center.x + radius * Math.cos(i * 2 * Math.PI / 6D));
            int yval = (int) (center.y + radius * Math.sin(i * 2 * Math.PI / 6D));
            msg.append(i + ":["+xval+","+yval+"]\n");
            polygon.addPoint(xval, yval);
        }
        this.info = this.info.concat(msg.toString());
        return polygon;
    }
    public void setFaceCount(int faces) {
    	this.hexagon = createHexagon(faces);
    }
    private Polygon createHexagon(int faces) {
        Polygon polygon = new Polygon();
        StringBuffer msg = new StringBuffer("\nPoints:\n");
        for (int i = 0; i < 6; i++) {
            int xval = (int) (center.x + radius * Math.cos(i * 2 * Math.PI / faces));
            int yval = (int) (center.y + radius * Math.sin(i * 2 * Math.PI / faces));
            msg.append(i + ":["+xval+","+yval+"]\n");
            polygon.addPoint(xval, yval);
        }
        this.info = this.info.concat(msg.toString());
        return polygon;
    }

    public int getRadius() {
        return radius;
    }

    public Point getCenter() {
        return center;
    }

    public Polygon getHexagon() {
        return hexagon;
    }
    public String getInfo(){
        return this.info;
    }

}

