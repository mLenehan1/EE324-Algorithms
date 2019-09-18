package Navigator;

import java.awt.Color;
import java.util.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.introcs.StdDraw;

public class JarvisMarch {
	
	private static int initialIndex;
	public static int collinear;
	public static boolean sourceChanged = false, destinationChanged = false;
	public static Point2D start = new Point2D(0,0); 
			
	private static Point2D getInitialPoint(Polygon2D polygon)
	{
		Point2D[] points = polygon.asPointsArray();
		Point2D initialPoint = points[0];
		for(int i = 0; i<points.length; i++)
		{
			if(points[i].getX()<initialPoint.getX() || (points[i].getX()==initialPoint.getX() && points[i].getY()>initialPoint.getY()))
			{
			initialPoint = points[i];
			initialIndex = i;
			}
		}
		return initialPoint;
	}
	
	private static Boolean isCounterClockwise(Point2D p1, Point2D p2, Point2D p3)
	{
		if((p3.getY()-p1.getY())*(p2.getX()-p3.getX())-(p3.getX()-p1.getX())*(p2.getY()-p3.getY())>0) 
			{
				collinear = 0;
				return true;
			}
		else if((p3.getY()-p1.getY())*(p2.getX()-p3.getX())-(p3.getX()-p1.getX())*(p2.getY()-p3.getY())==0)
		{
			collinear = 1;
			return false;
		}
		else 
			{
				collinear = 0;
				return false;
			}
	}
	
	private static Boolean isIntersecting(Point2D p1, Point2D p2, Point2D[] points)
	{
		Line2D l1 = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
		for(int i = 0; i<points.length-1; i++)
		{
			if(l1.getX1()==points[i].getX()&&l1.getY1()==points[i].getY()||l1.getX2()==points[i].getX()&&l1.getY2()==points[i].getY()) continue;
			for(int j = 1; j<points.length; j++)
			{
				if(l1.getX1()==points[j].getX()&&l1.getY1()==points[j].getY()||l1.getX2()==points[j].getX()&&l1.getY2()==points[j].getY() ||i==j) continue;
				Line2D l2 = new Line2D.Double(points[i].getX(), points[i].getY(), points[j].getX(), points[j].getY());
				if(l1.intersectsLine(l2)==true)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean collinearDistance(Point2D p1, Point2D p2, Point2D p3)
	{
		double dist1 = Math.sqrt(((p2.getX()-p1.getX())*(p2.getX()-p1.getX()))+((p2.getY()-p1.getY())*(p2.getY()-p1.getY())));
		double dist2 = Math.sqrt(((p3.getX()-p1.getX())*(p3.getX()-p1.getX()))+((p3.getY()-p1.getY())*(p3.getY()-p1.getY())));
		if(dist1<dist2) return true;
		return false;
	}
	
	private static double[][] hullGraph(Polygon2D hull, Point2D[] points)
	{
		double hullGraph[][] = new double[hull.size()][hull.size()];
		for(int i = 0; i<hull.size(); i++)
		{
			for(int j = 0; j<hull.size(); j++)
			{
				if(isIntersecting(points[i], points[j], points)==false &&i!=j)
				{
					hullGraph[i][j] = points[i].distanceTo(points[j]);	
				}
				else hullGraph[i][j] = 0;
			}
		}
		return hullGraph;
	}
	
	private static double[][] createGraph(Polygon2D hull, ObstacleMap map)
	{
		Point2D hullPoints[] = hull.asPointsArray();
		List<Point2D> points = new ArrayList<>();
		for(int i = 0; i<hullPoints.length; i++)
		{
			points.add(hullPoints[i]);
		}
		Point2D polygonPoints[] = map.shape().asPointsArray();
		int sourceInside = 0, destinationInside = 0, sourceNN = 0, destNN = 0;
		double sourceDist = Double.POSITIVE_INFINITY, destinationDist = Double.POSITIVE_INFINITY;
		Point2D destination = map.destinationPoint(), source = map.sourcePoint();
		double hullGraph[][] = hullGraph(hull, hullPoints);
		double graph[][] = new double[hull.size()+2][hull.size()+2];
			for(int i = 0; i<hullPoints.length; i++)
			{
				if(isIntersecting(source, hullPoints[i], polygonPoints)==false)
				{
					graph[0][i+1] = source.distanceTo(hullPoints[i]);	
					graph[i+1][0] = source.distanceTo(hullPoints[i]);
				}
				else 
				{
					graph[0][i+1] = 0;
					graph[i+1][0] = 0;
					sourceInside++;
				}
			}
		graph[0][graph.length-1] = 0;
		for(int i = 0; i<hullPoints.length; i++)
		{
			for(int j = 0; j<hullPoints.length; j++)
			{
				graph[i+1][j+1] = hullGraph[i][j];
				graph[j+1][i+1] = hullGraph[j][i];
			}
		}	
			for(int i = 0; i<hullPoints.length; i++)
			{
				if(isIntersecting(hullPoints[i], destination, polygonPoints)==false)
				{
					graph[graph.length-1][i+1] = destination.distanceTo(hullPoints[i]);
					graph[i+1][graph.length-1] = destination.distanceTo(hullPoints[i]);
				}
				else
				{
					graph[graph.length-1][i+1] = 0;
					graph[i+1][graph.length-1] = 0;
					destinationInside++;
				}
			}
		graph[graph.length-1][graph.length-1] = 0;
		if(sourceInside == graph.length-2)
		{
			for(int i = 0; i<hullPoints.length; i++)
			{
				if(source.distanceTo(hullPoints[i])<sourceDist)
				{
					sourceDist = source.distanceTo(hullPoints[i]);
					sourceNN = i;
				}
			}
			graph[0][sourceNN+1] = sourceDist = source.distanceTo(hullPoints[sourceNN]);
			graph[sourceNN+1][0] = sourceDist = source.distanceTo(hullPoints[sourceNN]);
		}
		if(destinationInside == graph.length-2)
		{
			for(int i = 0; i<hullPoints.length; i++)
			{
				if(destination.distanceTo(hullPoints[i])<destinationDist)
				{
					destinationDist = destination.distanceTo(hullPoints[i]);
					destNN = i;
				}
			}
			graph[graph.length-1][destNN+1] = destinationDist = destination.distanceTo(hullPoints[destNN]);
			graph[destNN+1][graph.length-1] = destinationDist = destination.distanceTo(hullPoints[destNN]);
		}
		return graph;
	}
	
	public static Polygon2D findConvexHull(Polygon2D polygon)
	{
		Point2D[] points = polygon.asPointsArray();
		Polygon2D hull = new Polygon2D();
		getInitialPoint(polygon);
		int currentIndex = initialIndex, nextIndex;
		while(true)
		{
			hull.addPoint(points[currentIndex]);
			nextIndex = (currentIndex+1)%points.length;
			for(int i=0; i<points.length; i++)
			{
				if(currentIndex == i) continue;
				if(isCounterClockwise(points[currentIndex], points[nextIndex], points[i])==true)
				{
					nextIndex = i;
				}
				else if(isCounterClockwise(points[currentIndex], points[nextIndex], points[i])==false && collinear == 1)
				{
					if(collinearDistance(points[currentIndex], points[nextIndex], points[i]) == true)
						nextIndex = i;
				}
			}
			currentIndex = nextIndex;
			if(currentIndex==initialIndex) break;
		}
		System.out.println(hull);
		
		return hull;
	}
	
	public static Polygon2D findShortestPath(ObstacleMap testMap, Polygon2D hull, Point2D startPoint, Point2D endPoint)
	{
		double graph[][] = createGraph(hull, testMap);
		double distance[] = new double[graph.length];
		for(int i = 0; i<graph.length; i++)
			distance[i] = 0;
		int nextIndex = 0;
		Polygon2D path = new Polygon2D();
		path.addPoint(testMap.sourcePoint());
		Point2D points[] = new Point2D[graph.length];
		List<Point2D> unvisited = new ArrayList<>();
		points[0] = testMap.sourcePoint();
		for(int i = 0; i<hull.size(); i++)
		{
			unvisited.add(hull.asPointsArray()[i]);
			points[i+1] = hull.asPointsArray()[i];
		}
		unvisited.add(testMap.destinationPoint());
		points[graph.length-1] = testMap.destinationPoint();
		
		for(int i = 0; i<graph.length; i++)
		{
			if(points[i].equals(testMap.destinationPoint())) break;
			double pathDistance = Double.POSITIVE_INFINITY;
			for(int j = 0; j<graph.length; j++)
			{
				if(i==j) continue;
				if(points[j].equals(testMap.destinationPoint()) && graph[i][j]!=0)
				{
					nextIndex = j;
				}
				else if(graph[i][j]!= 0 && unvisited.contains(points[j]) && points[j].distanceTo(testMap.destinationPoint())+graph[i][j]<pathDistance)
					{
					pathDistance = points[j].distanceTo(testMap.destinationPoint())+graph[i][j];
					nextIndex = j;
					}
			}
			System.out.println(nextIndex);
			unvisited.remove(points[nextIndex]);
			path.addPoint(points[nextIndex]);
			i = nextIndex-1;
		}

		StdDraw.setPenColor(Color.GREEN);
		path.drawPath();
		
		
		/*for(int i = 0; i<distance.length; i++)
		System.out.println(distance[i] + "DISTANCE!!!!");*/
		StdDraw.setPenColor(Color.RED);
		for(int i = 0; i<graph.length; i++)
		{
			for(int j = 0; j<graph.length; j++)
			{
				if(graph[i][j]!=0)
				{
				Polygon2D line = new Polygon2D();
				line.addPoint(points[i]);
				line.addPoint(points[j]);
				//line.draw();
				System.out.println(i + " " + graph[i][j] + "  " + j);
			}
			}
		}
			return path;
	}

	public static void main(String args[]) {
		/*ObstacleMap collinearMap = new ObstacleMap("COLLINEAR-TEST-MAP.TXT");
		collinearMap.drawMap();
		Polygon2D map = collinearMap.shape();*/
		/*ObstacleMap testMap1A = new ObstacleMap("TEST-MAP-1A.TXT");
		testMap1A.drawMap();
		Polygon2D map = testMap1A.shape();*/
		/*ObstacleMap testMap1B = new ObstacleMap("TEST-MAP-1B.TXT");
		testMap1B.drawMap();
		Polygon2D map = testMap1B.shape();*/
		ObstacleMap testMap1C = new ObstacleMap("TEST-MAP-1C.TXT");
		testMap1C.drawMap();
		Polygon2D map = testMap1C.shape();
		StdDraw.setPenColor(Color.RED);
		Polygon2D hull = findConvexHull(map);
		hull.draw();
		//Polygon2D path = findShortestPath(collinearMap, hull, collinearMap.sourcePoint(), collinearMap.destinationPoint());
		//Polygon2D path = findShortestPath(testMap1A, hull, testMap1A.sourcePoint(), testMap1A.destinationPoint());
		//Polygon2D path = findShortestPath(testMap1B, hull, testMap1B.sourcePoint(), testMap1B.destinationPoint());
		Polygon2D path = findShortestPath(testMap1C, hull, testMap1C.sourcePoint(), testMap1C.destinationPoint());
		}
}