
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

// JFrame is the window that is opened so you can draw/write on



public class ConvexHull extends JFrame 
    implements ActionListener, MouseListener {

    // The radius in pixels of the circles drawn in graph_panel

    final int NODE_RADIUS = 3;

    // GUI stuff
    CanvasPanel canvas = null; // class Dr. Doug created; it is the actual part you put the dots (area you draw stuff)

    JPanel buttonPanel = null;
    JButton drawButton, clearButton;
    
    // Data Structures for the Points

    /*This holds the set of vertices, all
     * represented as type Point.
     */
	// Point is a built-in java class; just as you'd imagine (x,y) coordinates
    LinkedList<Point> vertices = null;
	LinkedList<Point> hullPoints = null;
	
    // Event handling stuff
    Dimension panelDim = null;

    public ConvexHull() {
	super("Graham Scan Convex Hull");
	setSize(new Dimension(700,575));

	// Initialize main data structures
	initializeDataStructures(); // method that initializes the linked list

	//The content pane
		// you add things to the content pane, not the window; JUST how the library was programmed
	Container contentPane = getContentPane();
	contentPane.setLayout(new BoxLayout(contentPane, 
					    BoxLayout.Y_AXIS));

	//Create the drawing area
	canvas = new CanvasPanel(this);
	canvas.addMouseListener(this);

	Dimension canvasSize = new Dimension(700,500);
	canvas.setMinimumSize(canvasSize);
	canvas.setPreferredSize(canvasSize);
	canvas.setMaximumSize(canvasSize);
	canvas.setBackground(Color.black);

	// Create buttonPanel and fill it
	buttonPanel = new JPanel();
	Dimension panelSize = new Dimension(700,75);
	buttonPanel.setMinimumSize(panelSize);
	buttonPanel.setPreferredSize(panelSize);
	buttonPanel.setMaximumSize(panelSize);
	buttonPanel.setLayout(new BoxLayout(buttonPanel,
					    BoxLayout.X_AXIS));
	buttonPanel.
	    setBorder(BorderFactory.
		      createCompoundBorder(BorderFactory.
					   createLineBorder(Color.red),
					   buttonPanel.getBorder()));

	Dimension buttonSize = new Dimension(175,50);
	drawButton = new JButton("Convex Hull"); // label for GUI button to change colors
	drawButton.setMinimumSize(buttonSize);
	drawButton.setPreferredSize(buttonSize);
	drawButton.setMaximumSize(buttonSize);
	drawButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	drawButton.setActionCommand("convexHull"); // label for program to know which button has been clicked
	drawButton.addActionListener(this);
	drawButton.
	    setBorder(BorderFactory.
		      createCompoundBorder(BorderFactory.
					   createLineBorder(Color.green),
					   drawButton.getBorder()));
	
	clearButton = new JButton("Clear");
	clearButton.setMinimumSize(buttonSize);
	clearButton.setPreferredSize(buttonSize);
	clearButton.setMaximumSize(buttonSize);
	clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	clearButton.setActionCommand("clearDiagram");
	clearButton.addActionListener(this);
	clearButton.
	    setBorder(BorderFactory.
		      createCompoundBorder(BorderFactory.
					   createLineBorder(Color.blue),
					   clearButton.getBorder()));

	buttonPanel.add(Box.createHorizontalGlue()); // this is the spacing between the two buttons so it looks elegant
	buttonPanel.add(drawButton);
	buttonPanel.add(Box.createHorizontalGlue());
	buttonPanel.add(clearButton);
	buttonPanel.add(Box.createHorizontalGlue());

	contentPane.add(canvas);
	contentPane.add(buttonPanel);
    }

    public static void main(String[] args) {
	
	ConvexHull project = new ConvexHull();
	project.addWindowListener(new WindowAdapter() {
		public void 
		    windowClosing(WindowEvent e) {
		    System.exit(0);
		}
	    }
				  );
	project.pack(); // put everything in the window you created
	project.setVisible(true); // make the window visible
    }

	// actionPerformed is an event listener
    public void actionPerformed(ActionEvent e) {

	String buttonIdentifier = e.getActionCommand();

	if (buttonIdentifier.equals("convexHull")) {
	    // compute the points in the convex hull
		hullPoints = computeConvexHull(vertices);
	    // canvas.changeColor();
	    canvas.repaint(); // whenever you make any changes, you have to repaint the canvas
	} else if (buttonIdentifier.equals("clearDiagram")) {
	    vertices.clear();
		hullPoints.clear();
	    canvas.repaint();
	} 
    }

    public void mouseClicked(MouseEvent e) {
	Point click_point = e.getPoint();
	vertices.add(click_point);
	canvas.repaint();
    }

    public void initializeDataStructures() {
	vertices = new LinkedList<Point>();
	hullPoints = new LinkedList<Point>();
    }

    public void mouseExited(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {}
	// NB: you have to include all the mouse event listeners, for the code to work, even if it is not being used (yeah, i know).

	/**
	 * Computes the convex hull of a set of points using the Graham Scan algorithm.
	 * Returns the points in the convex hull in counter-clockwise order.
	 */
	private LinkedList<Point> computeConvexHull(LinkedList<Point> inputPoints) {
		LinkedList<Point> result = new LinkedList<Point>();

		// EDGE CASE IMPLEMENTATION UNNEEDED (handled in code already)
		// 1. if we have only one point, return that single point in a different color
		// 2. if user inputs only two points, return a single line connecting the two points.
		// if the input size is less than 3, just return result = inputPoints, so code will immediately join a line between them
		// error: for a single point, the color does not get changed;
		// 		  for more than one point, the convex hull is updated as the user inputs vertices, and not after they click "Convex Hull"

		// if (inputPoints.size() < 3) { 
		// 	return inputPoints;
		// }

		// find the point with the lowest y-coordinate (and leftmost if ties)
		Point p0 = findLowestYPoint(inputPoints);

		// sort the points by polar angle with p0
		// copy them into an array so we can sort them
		List<Point> sorted = new ArrayList<>(inputPoints);
		sorted.remove(p0); // remove p0 from the list of points to sort

		sorted.sort((p1, p2) -> {
			int orient = orientation(p0, p1, p2);
            if (orient == 0) {
                // Collinear; closer one to p0 should come first
                double dist1 = p0.distanceSq(p1);
                double dist2 = p0.distanceSq(p2);
                return Double.compare(dist1, dist2);
            }
            // orientation(p0, p1, p2) < 0 => p1 is more CCW => negative => put p1 before p2
            return (orient < 0) ? -1 : 1; 
		});

		// build the hull using a stack
        Stack<Point> stack = new Stack<>();
        stack.push(p0);

        for (Point p : sorted) {
            // pop while the turn formed by [nextToTop, top, p] is not CCW
            while (stack.size() > 1 &&
                   orientation(nextToTop(stack), stack.peek(), p) >= 0) {
                // >= 0 => orientation is 0 or > 0 => collinear or clockwise => pop
                stack.pop();
            }
            stack.push(p);
        }

        // copy stack to our result list
        result.addAll(stack);
        return result;
    }

	// returns the orientation of the triplet pairs of points
	// we calculate the cross product of the two vectors, and use the sign of the result to determine if CW or CCW
	private int orientation(Point p, Point q, Point r) {
        long cross = (long)(q.y - p.y)*(r.x - q.x)
                   - (long)(q.x - p.x)*(r.y - q.y);
        if (cross == 0) return 0;         // collinear
        return (cross > 0) ? 1 : -1;      // > 0 => clockwise, < 0 => counter-cw
    }

    // gets the second item from the top of a stack, without removing the top. 
    private Point nextToTop(Stack<Point> stack) {
        Point top = stack.pop();
        Point belowTop = stack.peek();
        stack.push(top);
        return belowTop;
    }

    // finds the point with the lowest y in the list (or leftmost if there is a tie).
    private Point findLowestYPoint(LinkedList<Point> points) {
        Point best = points.getFirst();
        for (Point p : points) {
            if (p.y < best.y) {
                best = p;
            } else if (p.y == best.y && p.x < best.x) {
                best = p;
            }
        }
        return best;
    }


	}

