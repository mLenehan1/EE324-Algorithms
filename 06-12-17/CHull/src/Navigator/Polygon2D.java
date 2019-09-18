package Navigator;
// Polygon2D.java
// C.McArdle, DCU, 2017

import java.awt.Color;
import java.util.Arrays;
import edu.princeton.cs.introcs.StdDraw;

public class Polygon2D {

	private Point2D[] vertices;

	private final double LINE_DRAW_PEN_WIDTH  = 0.003;
	private final double POINT_DRAW_PEN_WIDTH = 0.015;

	public Polygon2D() {  // default constructor
		vertices = new Point2D[0];
	}

	public Polygon2D(Point2D[] points) {  // constructor
		vertices = new Point2D[points.length];
		for (int i=0; i<points.length; i++) {
			vertices[i] = new Point2D(points[i]);
		}
	}

	public Polygon2D(Polygon2D poly) {  // copy constructor
		vertices = new Point2D[poly.vertices.length];
		for (int i=0; i<poly.vertices.length; i++) {
			vertices[i] = new Point2D(poly.vertices[i]);
		}
	}

	public void addPoint(Point2D p) {
		vertices = Arrays.copyOf(vertices, vertices.length + 1);
		vertices[vertices.length-1] = new Point2D(p);
	}

	public int size() {
		return vertices.length;
	}
	
	public Point2D getIndex(int indx) {  // return point at given index
		if ((indx >= 0) && (indx < vertices.length))
			return new Point2D(vertices[indx]); // make copy of point object
		else
			return null;
	}
	
	public Point2D[] asPointsArray() {  // return all points in the polygon
		return Arrays.copyOf(vertices, vertices.length);
	}
	
	public void draw() {
		StdDraw.setPenRadius(LINE_DRAW_PEN_WIDTH);
		for (int i=0; i<vertices.length; i++) {
			if (i < vertices.length-1)
				StdDraw.line(vertices[i].getX(), vertices[i].getY(), vertices[i+1].getX(), vertices[i+1].getY());
			else
				StdDraw.line(vertices[i].getX(), vertices[i].getY(), vertices[0].getX(), vertices[0].getY());
		}
	}
	
	public void drawPath() {
		StdDraw.setPenRadius(LINE_DRAW_PEN_WIDTH);
		for (int i=0; i<vertices.length-1; i++) {
			if (i < vertices.length-1)
				StdDraw.line(vertices[i].getX(), vertices[i].getY(), vertices[i+1].getX(), vertices[i+1].getY());
			else
				StdDraw.line(vertices[i].getX(), vertices[i].getY(), vertices[0].getX(), vertices[0].getY());
		}
	}

	public void drawFilled() {
		StdDraw.setPenRadius(LINE_DRAW_PEN_WIDTH);
		double[] X = new double[vertices.length];
		double[] Y = new double[vertices.length];
		for (int i=0; i<vertices.length; i++) {
			X[i] = vertices[i].getX();
			Y[i] = vertices[i].getY();
		}
		StdDraw.filledPolygon(X, Y);
	}

	public void drawVertices() {
		StdDraw.setPenRadius(POINT_DRAW_PEN_WIDTH);
		for (int i=0; i<vertices.length; i++) {
			vertices[i].draw();
		}
	}
	
	@Override
	public String toString() {
		String polyAsString = "";
		for (int i=0; i<vertices.length; i++)
			polyAsString += vertices[i].toString() + "\n"; 
		return polyAsString;
	}
	
	// TEST CLIENT //
	public static void main(String args[]) {
		Point2D[] shape = new Point2D[4];
		shape[0] = new Point2D(0.2,0.2);
		shape[1] = new Point2D(0.2,0.8);
		shape[2] = new Point2D(0.8,0.8);
		shape[3] = new Point2D(0.8,0.2);
		Polygon2D pg = new Polygon2D(shape);
		StdDraw.setCanvasSize(800, 800);
		StdDraw.setPenColor(Color.BLUE);
		pg.draw();
		StdDraw.setPenColor(Color.RED);
		pg.drawVertices();
	}
}
