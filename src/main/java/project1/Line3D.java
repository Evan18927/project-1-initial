package project1;

import java.util.logging.Logger;

;

/**
 * Represents a line in 3D space defined by two endpoints.
 * 
 * <p>This class demonstrates several object-oriented design patterns:
 * <ul>
 *   <li><b>Immutability Pattern:</b> Once created, a Line3D cannot be modified, ensuring thread-safety
 *       and preventing unintended side effects. New lines must be created for modifications.</li>
 *   <li><b>Value Object Pattern:</b> Line3D represents a mathematical concept with no identity beyond
 *       its values. Equality is based on coordinates, not object identity.</li>
 *   <li><b>Defensive Copying:</b> Point3D objects are copied during construction to prevent external
 *       modification of internal state.</li>
 *   <li><b>Fail-Fast Validation:</b> Constructor validates inputs immediately, throwing exceptions
 *       for invalid states (e.g., degenerate lines where start equals end).</li>
 * </ul>
 * 
 * <p>These patterns align with foundational principles:
 * <ul>
 *   <li><b>Encapsulation:</b> Internal state is private and accessed only through public methods</li>
 *   <li><b>Single Responsibility:</b> Class focuses solely on 3D line representation and operations</li>
 *   <li><b>Open/Closed Principle:</b> Class is open for extension (subclassing) but closed for
 *       modification due to immutability</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 */
public class Line3D {
    
    private static final Logger logger = LoggerFactory.getLogger(Line3D.class);
    
    // Immutable fields representing the two endpoints of the line
    private final Point3D start;
    private final Point3D end;
    
    // Cached length to avoid recalculation (lazy initialization pattern)
    private Double cachedLength = null;
    
    /**
     * Constructs a new Line3D from two Point3D objects.
     * 
     * <p>This constructor demonstrates the <b>Defensive Copying</b> pattern by creating
     * copies of the provided points to ensure the line's state cannot be modified externally.
     * It also implements <b>Fail-Fast Validation</b> to catch invalid inputs immediately.
     * 
     * @param start the starting point of the line
     * @param end the ending point of the line
     * @throws IllegalArgumentException if either point is null or if start equals end
     */
    public Line3D(Point3D start, Point3D end) {
        logger.info("Creating Line3D with start: {} and end: {}");
        
        // Validate inputs
        if (start == null || end == null) {
            logger.log("Attempted to create Line3D with null point(s)");
            throw new IllegalArgumentException("Start and end points cannot be null");
        }
        
        if (start.equals(end)) {
            logger.log("Attempted to create degenerate Line3D where start equals end: {}", start);
            throw new IllegalArgumentException("Start and end points must be different (degenerate line not allowed)");
        }
        
        // Defensive copying to prevent external modification
        this.start = new Point3D(start.getX(), start.getY(), start.getZ());
        this.end = new Point3D(end.getX(), end.getY(), end.getZ());
        
        logger.info("Successfully created Line3D");
    }
    
    /**
     * Convenience constructor using coordinate values directly.
     * 
     * @param x1 x-coordinate of start point
     * @param y1 y-coordinate of start point
     * @param z1 z-coordinate of start point
     * @param x2 x-coordinate of end point
     * @param y2 y-coordinate of end point
     * @param z2 z-coordinate of end point
     */
    public Line3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        this(new Point3D(x1, y1, z1), new Point3D(x2, y2, z2));
    }
    
    /**
     * Returns the starting point of the line.
     * 
     * <p>Returns a copy to maintain immutability (defensive copying pattern).
     * 
     * @return a copy of the start point
     */
    public Point3D getStart() {
        logger.debug("Retrieving start point: {}", start);
        return new Point3D(start.getX(), start.getY(), start.getZ());
    }
    
    /**
     * Returns the ending point of the line.
     * 
     * <p>Returns a copy to maintain immutability (defensive copying pattern).
     * 
     * @return a copy of the end point
     */
    public Point3D getEnd() {
        logger.debug("Retrieving end point: {}", end);
        return new Point3D(end.getX(), end.getY(), end.getZ());
    }
    
    /**
     * Calculates and returns the length of the line using the Euclidean distance formula.
     * 
     * <p>This method implements the <b>Lazy Initialization</b> pattern by caching the computed
     * length after first calculation to avoid redundant computations.
     * 
     * <p>Formula: length = √[(x₂-x₁)² + (y₂-y₁)² + (z₂-z₁)²]
     * 
     * @return the length of the line
     */
    public double length() {
        logger.debug("Calculating length of line");
        
        if (cachedLength == null) {
            logger.debug("Length not cached, computing...");
            double dx = end.getX() - start.getX();
            double dy = end.getY() - start.getY();
            double dz = end.getZ() - start.getZ();
            
            cachedLength = Math.sqrt(dx * dx + dy * dy + dz * dz);
            logger.info("Computed line length: {}", cachedLength);
        } else {
            logger.debug("Using cached length: {}", cachedLength);
        }
        
        return cachedLength;
    }
    
    /**
     * Calculates the shortest distance from this line to another line in 3D space.
     * 
     * <p>This method uses the formula for distance between two skew lines in 3D:
     * <pre>
     * distance = |((P₂ - P₁) · (d₁ × d₂))| / |d₁ × d₂|
     * </pre>
     * where P₁, P₂ are points on the lines and d₁, d₂ are direction vectors.
     * 
     * <p>Special cases:
     * <ul>
     *   <li>Parallel lines: Uses point-to-line distance</li>
     *   <li>Intersecting lines: Returns 0</li>
     * </ul>
     * 
     * @param other the other line to calculate distance to
     * @return the shortest distance between the two lines
     * @throws IllegalArgumentException if other is null
     */
    public double distanceToLine(Line3D other) {
        logger.info("Calculating distance to another line");
        
        if (other == null) {
            logger.error("Attempted to calculate distance to null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }
        
        // Direction vectors
        Vector3D d1 = new Vector3D(
            this.end.getX() - this.start.getX(),
            this.end.getY() - this.start.getY(),
            this.end.getZ() - this.start.getZ()
        );
        
        Vector3D d2 = new Vector3D(
            other.end.getX() - other.start.getX(),
            other.end.getY() - other.start.getY(),
            other.end.getZ() - other.start.getZ()
        );
        
        // Cross product of direction vectors
        Vector3D cross = d1.cross(d2);
        double crossMagnitude = cross.magnitude();
        
        // Check if lines are parallel (cross product is zero)
        if (crossMagnitude < 1e-10) {
            logger.warn("Lines are parallel, using point-to-line distance");
            return distanceToPoint(other.start);
        }
        
        // Vector from start of this line to start of other line
        Vector3D w = new Vector3D(
            other.start.getX() - this.start.getX(),
            other.start.getY() - this.start.getY(),
            other.start.getZ() - this.start.getZ()
        );
        
        // Distance formula for skew lines
        double distance = Math.abs(w.dot(cross)) / crossMagnitude;
        
        logger.info("Distance between lines: {}", distance);
        return distance;
    }
    
    /**
     * Calculates the shortest distance from a point to this line.
     * 
     * <p>Uses the formula: distance = ||(P - P₁) × d|| / ||d||
     * where P is the point, P₁ is the start of the line, and d is the direction vector.
     * 
     * @param point the point to calculate distance to
     * @return the perpendicular distance from the point to the line
     * @throws IllegalArgumentException if point is null
     */
    public double distanceToPoint(Point3D point) {
        logger.info("Calculating distance from line to point: {}", point);
        
        if (point == null) {
            logger.error("Attempted to calculate distance to null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        // Direction vector of the line
        Vector3D direction = new Vector3D(
            end.getX() - start.getX(),
            end.getY() - start.getY(),
            end.getZ() - start.getZ()
        );
        
        // Vector from line start to the point
        Vector3D toPoint = new Vector3D(
            point.getX() - start.getX(),
            point.getY() - start.getY(),
            point.getZ() - start.getZ()
        );
        
        // Cross product gives area of parallelogram
        Vector3D cross = toPoint.cross(direction);
        
        // Distance is area divided by base
        double distance = cross.magnitude() / direction.magnitude();
        
        logger.info("Distance from line to point: {}", distance);
        return distance;
    }
    
    /**
     * Returns the direction vector of the line (normalized).
     * 
     * <p>The direction vector points from start to end and has unit length.
     * 
     * @return the normalized direction vector
     */
    public Vector3D getDirectionVector() {
        logger.log("Getting direction vector");
        
        Vector3D direction = new Vector3D(
            end.getX() - start.getX(),
            end.getY() - start.getY(),
            end.getZ() - start.getZ()
        );
        
        return direction.normalize();
    }
    
    /**
     * Returns the midpoint of the line.
     * 
     * @return the point exactly halfway between start and end
     */
    public Point3D getMidpoint() {
        logger.debug("Calculating midpoint");
        
        return new Point3D(
            (start.getX() + end.getX()) / 2.0,
            (start.getY() + end.getY()) / 2.0,
            (start.getZ() + end.getZ()) / 2.0
        );
    }
    
    /**
     * Calculates a point along the line at parameter t.
     * 
     * <p>Parametric equation: P(t) = P₁ + t(P₂ - P₁)
     * <ul>
     *   <li>t = 0 returns the start point</li>
     *   <li>t = 1 returns the end point</li>
     *   <li>0 < t < 1 returns a point between start and end</li>
     *   <li>t < 0 or t > 1 extends beyond the line segment</li>
     * </ul>
     * 
     * @param t the parameter value
     * @return the point at parameter t
     */
    public Point3D getPointAt(double t) {
        logger.debug("Getting point at parameter t = {}", t);
        
        if (t < 0 || t > 1) {
            logger.warn("Parameter t = {} is outside [0, 1], point will be beyond line segment", t);
        }
        
        return new Point3D(
            start.getX() + t * (end.getX() - start.getX()),
            start.getY() + t * (end.getY() - start.getY()),
            start.getZ() + t * (end.getZ() - start.getZ())
        );
    }
    
    /**
     * Checks if a point lies on this line within a tolerance.
     * 
     * @param point the point to check
     * @param tolerance the maximum allowed distance for the point to be considered on the line
     * @return true if the point is on the line within tolerance
     */
    public boolean containsPoint(Point3D point, double tolerance) {
        logger.debug("Checking if line contains point {} within tolerance {}", point, tolerance);
        
        if (point == null) {
            logger.error("Cannot check if line contains null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        if (tolerance < 0) {
            logger.error("Tolerance must be non-negative");
            throw new IllegalArgumentException("Tolerance must be non-negative");
        }
        
        double distance = distanceToPoint(point);
        boolean contains = distance <= tolerance;
        
        logger.info("Point {} {} on line (distance: {}, tolerance: {})", 
                   point, contains ? "is" : "is not", distance, tolerance);
        
        return contains;
    }
    
    /**
     * Checks if this line is parallel to another line within a tolerance.
     * 
     * <p>Two lines are parallel if their direction vectors are parallel,
     * which occurs when their cross product is approximately zero.
     * 
     * @param other the other line
     * @param tolerance the tolerance for considering vectors parallel
     * @return true if the lines are parallel
     */
    public boolean isParallel(Line3D other, double tolerance) {
        logger.debug("Checking if lines are parallel");
        
        if (other == null) {
            logger.error("Cannot check parallelism with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }
        
        Vector3D d1 = getDirectionVector();
        Vector3D d2 = other.getDirectionVector();
        
        double crossMagnitude = d1.cross(d2).magnitude();
        boolean parallel = crossMagnitude < tolerance;
        
        logger.info("Lines {} parallel (cross magnitude: {}, tolerance: {})",
                   parallel ? "are" : "are not", crossMagnitude, tolerance);
        
        return parallel;
    }
    
    /**
     * Checks if this line is perpendicular to another line within a tolerance.
     * 
     * <p>Two lines are perpendicular if their direction vectors are perpendicular,
     * which occurs when their dot product is approximately zero.
     * 
     * @param other the other line
     * @param tolerance the tolerance for considering vectors perpendicular
     * @return true if the lines are perpendicular
     */
    public boolean isPerpendicular(Line3D other, double tolerance) {
        logger.debug("Checking if lines are perpendicular");
        
        if (other == null) {
            logger.error("Cannot check perpendicularity with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }
        
        Vector3D d1 = getDirectionVector();
        Vector3D d2 = other.getDirectionVector();
        
        double dotProduct = Math.abs(d1.dot(d2));
        boolean perpendicular = dotProduct < tolerance;
        
        logger.info("Lines {} perpendicular (dot product: {}, tolerance: {})",
                   perpendicular ? "are" : "are not", dotProduct, tolerance);
        
        return perpendicular;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Line3D other = (Line3D) obj;
        return this.start.equals(other.start) && this.end.equals(other.end);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("Line3D[start=%s, end=%s, length=%.4f]", start, end, length());
    }
}

/**
 * Helper class representing a 3D vector for geometric calculations.
 * 
 * <p>This class is package-private as it's an implementation detail of Line3D.
 * It demonstrates the <b>Helper/Utility Object</b> pattern to encapsulate
 * vector operations.
 */
class Vector3D {
    private final double x;
    private final double y;
    private final double z;
    
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Computes the dot product with another vector.
     * 
     * @param other the other vector
     * @return the dot product
     */
    public double dot(Vector3D other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }
    
    /**
     * Computes the cross product with another vector.
     * 
     * @param other the other vector
     * @return a new vector representing the cross product
     */
    public Vector3D cross(Vector3D other) {
        return new Vector3D(
            this.y * other.z - this.z * other.y,
            this.z * other.x - this.x * other.z,
            this.x * other.y - this.y * other.x
        );
    }
    
    /**
     * Calculates the magnitude (length) of this vector.
     * 
     * @return the magnitude
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    /**
     * Returns a normalized (unit length) version of this vector.
     * 
     * @return the normalized vector
     */
    public Vector3D normalize() {
        double mag = magnitude();
        if (mag < 1e-10) {
            throw new ArithmeticException("Cannot normalize zero vector");
        }
        return new Vector3D(x / mag, y / mag, z / mag);
    }
    
    @Override
    public String toString() {
        return String.format("Vector3D[%.4f, %.4f, %.4f]", x, y, z);
    }
}