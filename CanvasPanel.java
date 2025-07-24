
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.*;

public class CanvasPanel extends JPanel {

    ConvexHull parent = null;
    LinkedList vertices = null;
    Color currentColor = Color.red;

    public CanvasPanel(ConvexHull _parent) {
	super();
	parent = _parent;
	vertices = parent.vertices;
    }

    public void paintComponent(Graphics g) {
	super.paintComponent(g);

	g.setColor(currentColor);

	ListIterator iterator = vertices.listIterator(0);

	Point currentVertex = null;

	// you can also draw other things
	for (int i=0; i < vertices.size(); ++i) {
	    currentVertex = (Point) iterator.next();
	    g.fillOval(currentVertex.x - parent.NODE_RADIUS,
		       currentVertex.y - parent.NODE_RADIUS,
		       2*parent.NODE_RADIUS, 2*parent.NODE_RADIUS);
	}

	g.setColor(Color.yellow);

	LinkedList<Point> hull = parent.hullPoints;
	for (int i = 0; i < hull.size(); i++) {
		Point p1 = hull.get(i);
		// this grabs the next hull point; but if i is the last index, if i is the last point in the list,... 
		// we will make p2 the first point so that line wraps around and "closes" the polygon
		Point p2 = hull.get((i + 1) % hull.size()); 
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	
	}

    public void changeColor() {
	if (currentColor.equals(Color.red)) {
	    currentColor = Color.yellow;
	} else {
	    currentColor = Color.red;
	}
    }
}


// TO RUN
// Compile:
// javac SwingShell.java

// Create a tar file: tar -cvf SwingShell.tar *.java
// Extract a tar file: tar -xvf SwingShell.tar
// Meaning: c = create; f = defualt file name; v = do it verbosely, tell me what you are adding; x = extract

// you want linked list of vertices, and a linked list of hull points, then after you have the hull points,
// then you use a drawing tool to connect the points



