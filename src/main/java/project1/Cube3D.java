package project1;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;

/**
 * Represents a cube in 3D space defined by its center point and side length.
 * 
 * <p>This class demonstrates several object-oriented design patterns:
 * <ul>
 *   <li><b>Immutability Pattern:</b> Once created, a Cube3D cannot be modified directly.
 *       Operations like rotation and translation return new Cube3D instances.</li>
 *   <li><b>Value Object Pattern:</b> Cube3D represents a geometric concept with equality
 *       based on center position, side length, and orientation.</li>
 *   <li><b>Composite Pattern:</b> Cube is composed of 8 vertices (Point3D) and 12 edges (Line3D),
 *       demonstrating composition over inheritance.</li>
 *   <li><b>Factory Method Pattern:</b> Multiple static factory methods provide different ways
 *       to construct cubes (from center, from corner, from bounds).</li>
 *   <li><b>Lazy Initialization:</b> Expensive calculations like volume and edges are computed
 *       once and cached for performance.</li>
 * </ul>
 * 
 * <p>These patterns align with foundational principles:
 * <ul>
 *   <li><b>Encapsulation:</b> Internal state is private; access through public methods</li>
 *   <li><b>Single Responsibility:</b> Focuses solely on 3D cube representation and operations</li>
 *   <li><b>DRY (Don't Repeat Yourself):</b> Reuses Point3D and Line3D classes</li>
 *   <li><b>Separation of Concerns:</b> Delegates point and line operations to respective classes</li>
 * </ul>
 * 
 * <p>Coordinate System: Uses right-handed coordinate system with standard orientation:
 * <ul>
 *   <li>X-axis: Right (positive) / Left (negative)</li>
 *   <li>Y-axis: Up (positive) / Down (negative)</li>
 *   <li>Z-axis: Forward (positive) / Backward (negative)</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 */
public class Cube3D {
    
    private static final Logger logger = Logger.getLogger(Cube3D.class.getName());
    
    // Core properties of the cube
    private final Point3D center;
    private final double sideLength;
    
    // Rotation angles in radians (Euler angles: roll, pitch, yaw)
    private final double rotationX;  // Rotation around X-axis (pitch)
    private final double rotationY;  // Rotation around Y-axis (yaw)
    private final double rotationZ;  // Rotation around Z-axis (roll)
    
    // Cached computed values (lazy initialization pattern)
    private Point3D[] cachedVertices = null;
    private Line3D[] cachedEdges = null;
    private Double cachedVolume = null;
    private Double cachedSurfaceArea = null;
    private Double cachedPerimeter = null;
    
    /**
     * Primary constructor for Cube3D.
     * 
     * <p>This constructor demonstrates <b>Fail-Fast Validation</b> and creates
     * an axis-aligned cube (no rotation) at the specified center.
     * 
     * @param center the center point of the cube
     * @param sideLength the length of each side of the cube
     * @throws IllegalArgumentException if center is null or sideLength is non-positive
     */
    public Cube3D(Point3D center, double sideLength) {
        this(center, sideLength, 0, 0, 0);
    }
    
    /**
     * Constructor with rotation support.
     * 
     * <p>Creates a cube with specified rotation angles. This demonstrates the
     * <b>Builder Pattern</b> concept where complex objects can be constructed
     * with various configurations.
     * 
     * @param center the center point of the cube
     * @param sideLength the length of each side of the cube
     * @param rotationX rotation around X-axis in radians
     * @param rotationY rotation around Y-axis in radians
     * @param rotationZ rotation around Z-axis in radians
     * @throws IllegalArgumentException if center is null or sideLength is non-positive
     */
    public Cube3D(Point3D center, double sideLength, double rotationX, double rotationY, double rotationZ) {
        logger.log(Level.INFO, "Creating Cube3D with center: {0}, side length: {1}", 
                  new Object[]{center, sideLength});
        
        // Validation
        if (center == null) {
            logger.log(Level.SEVERE, "Attempted to create Cube3D with null center");
            throw new IllegalArgumentException("Center point cannot be null");
        }
        
        if (sideLength <= 0) {
            logger.log(Level.SEVERE, "Attempted to create Cube3D with non-positive side length: {0}", sideLength);
            throw new IllegalArgumentException("Side length must be positive");
        }
        
        // Defensive copying for center
        this.center = new Point3D(center.getX(), center.getY(), center.getZ());
        this.sideLength = sideLength;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        
        logger.log(Level.INFO, "Successfully created Cube3D");
    }
    
    // ==================== FACTORY METHODS ====================
    
    /**
     * Creates a cube from a corner point (minimum corner) and side length.
     * 
     * <p>This is a <b>Factory Method</b> that provides an alternative way to construct
     * a cube. The corner represents the point with minimum x, y, z coordinates.
     * 
     * @param corner the minimum corner of the cube
     * @param sideLength the side length
     * @return a new Cube3D instance
     */
    public static Cube3D fromCorner(Point3D corner, double sideLength) {
        logger.log(Level.INFO, "Creating cube from corner: {0}", corner);
        
        if (corner == null) {
            throw new IllegalArgumentException("Corner cannot be null");
        }
        
        // Calculate center from corner
        double halfSide = sideLength / 2.0;
        Point3D center = new Point3D(
            corner.getX() + halfSide,
            corner.getY() + halfSide,
            corner.getZ() + halfSide
        );
        
        return new Cube3D(center, sideLength);
    }
    
    /**
     * Creates a cube that fits within specified bounds.
     * 
     * <p>Another <b>Factory Method</b> for flexible cube creation.
     * 
     * @param minCorner the minimum corner (smallest x, y, z)
     * @param maxCorner the maximum corner (largest x, y, z)
     * @return a new Cube3D instance
     * @throws IllegalArgumentException if corners are invalid
     */
    public static Cube3D fromBounds(Point3D minCorner, Point3D maxCorner) {
        logger.log(Level.INFO, "Creating cube from bounds");
        
        if (minCorner == null || maxCorner == null) {
            throw new IllegalArgumentException("Corners cannot be null");
        }
        
        // Calculate dimensions
        double dx = maxCorner.getX() - minCorner.getX();
        double dy = maxCorner.getY() - minCorner.getY();
        double dz = maxCorner.getZ() - minCorner.getZ();
        
        // Use smallest dimension for cube
        double sideLength = Math.min(Math.min(dx, dy), dz);
        
        if (sideLength <= 0) {
            throw new IllegalArgumentException("Invalid bounds for cube");
        }
        
        // Calculate center
        Point3D center = new Point3D(
            (minCorner.getX() + maxCorner.getX()) / 2.0,
            (minCorner.getY() + maxCorner.getY()) / 2.0,
            (minCorner.getZ() + maxCorner.getZ()) / 2.0
        );
        
        return new Cube3D(center, sideLength);
    }
    
    // ==================== BASIC GETTERS ====================
    
    /**
     * Returns the center point of the cube.
     * 
     * <p>Uses <b>Defensive Copying</b> to maintain immutability.
     * 
     * @return a copy of the center point
     */
    public Point3D getCenter() {
        logger.log(Level.FINE, "Retrieving center point");
        return new Point3D(center.getX(), center.getY(), center.getZ());
    }
    
    /**
     * Returns the side length of the cube.
     * 
     * @return the side length
     */
    public double getSideLength() {
        return sideLength;
    }
    
    /**
     * Returns the rotation around the X-axis in radians.
     * 
     * @return rotation in radians
     */
    public double getRotationX() {
        return rotationX;
    }
    
    /**
     * Returns the rotation around the Y-axis in radians.
     * 
     * @return rotation in radians
     */
    public double getRotationY() {
        return rotationY;
    }
    
    /**
     * Returns the rotation around the Z-axis in radians.
     * 
     * @return rotation in radians
     */
    public double getRotationZ() {
        return rotationZ;
    }
    
    // ==================== GEOMETRIC CALCULATIONS ====================
    
    /**
     * Calculates and returns the volume of the cube.
     * 
     * <p>Uses <b>Lazy Initialization</b> pattern with caching for performance.
     * 
     * <p>Formula: volume = side³
     * 
     * @return the volume of the cube
     */
    public double getVolume() {
        logger.log(Level.FINE, "Calculating volume");
        
        if (cachedVolume == null) {
            cachedVolume = Math.pow(sideLength, 3);
            logger.log(Level.INFO, "Computed cube volume: {0}", cachedVolume);
        } else {
            logger.log(Level.FINE, "Using cached volume: {0}", cachedVolume);
        }
        
        return cachedVolume;
    }
    
    /**
     * Calculates and returns the surface area of the cube.
     * 
     * <p>Formula: surface area = 6 × side²
     * 
     * @return the surface area
     */
    public double getSurfaceArea() {
        logger.log(Level.FINE, "Calculating surface area");
        
        if (cachedSurfaceArea == null) {
            cachedSurfaceArea = 6 * Math.pow(sideLength, 2);
            logger.log(Level.INFO, "Computed cube surface area: {0}", cachedSurfaceArea);
        } else {
            logger.log(Level.FINE, "Using cached surface area: {0}", cachedSurfaceArea);
        }
        
        return cachedSurfaceArea;
    }
    
    /**
     * Calculates the total perimeter (sum of all edge lengths).
     * 
     * <p>A cube has 12 edges, so perimeter = 12 × side length
     * 
     * @return the total perimeter
     */
    public double getPerimeter() {
        logger.log(Level.FINE, "Calculating perimeter");
        
        if (cachedPerimeter == null) {
            cachedPerimeter = 12 * sideLength;
            logger.log(Level.INFO, "Computed cube perimeter: {0}", cachedPerimeter);
        } else {
            logger.log(Level.FINE, "Using cached perimeter: {0}", cachedPerimeter);
        }
        
        return cachedPerimeter;
    }
    
    /**
     * Calculates the diagonal length of the cube (space diagonal).
     * 
     * <p>Formula: diagonal = side × √3
     * 
     * @return the space diagonal length
     */
    public double getDiagonal() {
        logger.log(Level.FINE, "Calculating space diagonal");
        return sideLength * Math.sqrt(3);
    }
    
    /**
     * Calculates the face diagonal length.
     * 
     * <p>Formula: face diagonal = side × √2
     * 
     * @return the face diagonal length
     */
    public double getFaceDiagonal() {
        logger.log(Level.FINE, "Calculating face diagonal");
        return sideLength * Math.sqrt(2);
    }
    
    // ==================== VERTEX OPERATIONS ====================
    
    /**
     * Returns all 8 vertices of the cube.
     * 
     * <p>Vertices are calculated considering rotation. This demonstrates the
     * <b>Composite Pattern</b> where the cube is composed of Point3D objects.
     * 
     * <p>Vertex ordering (for axis-aligned cube):
     * <pre>
     * [0] = (x-h, y-h, z-h) - bottom-back-left
     * [1] = (x+h, y-h, z-h) - bottom-back-right
     * [2] = (x+h, y+h, z-h) - top-back-right
     * [3] = (x-h, y+h, z-h) - top-back-left
     * [4] = (x-h, y-h, z+h) - bottom-front-left
     * [5] = (x+h, y-h, z+h) - bottom-front-right
     * [6] = (x+h, y+h, z+h) - top-front-right
     * [7] = (x-h, y+h, z+h) - top-front-left
     * </pre>
     * 
     * @return array of 8 vertices
     */
    public Point3D[] getVertices() {
        logger.log(Level.FINE, "Calculating vertices");
        
        if (cachedVertices == null) {
            logger.log(Level.FINE, "Vertices not cached, computing...");
            
            double h = sideLength / 2.0;
            Point3D[] vertices = new Point3D[8];
            
            // Define vertices in local coordinates (before rotation)
            double[][] localCoords = {
                {-h, -h, -h}, {h, -h, -h}, {h, h, -h}, {-h, h, -h},
                {-h, -h, h},  {h, -h, h},  {h, h, h},  {-h, h, h}
            };
            
            // Apply rotation and translation
            for (int i = 0; i < 8; i++) {
                double[] rotated = rotatePoint(localCoords[i][0], localCoords[i][1], localCoords[i][2]);
                vertices[i] = new Point3D(
                    center.getX() + rotated[0],
                    center.getY() + rotated[1],
                    center.getZ() + rotated[2]
                );
            }
            
            cachedVertices = vertices;
            logger.log(Level.INFO, "Computed cube vertices");
        } else {
            logger.log(Level.FINE, "Using cached vertices");
        }
        
        // Return defensive copy
        return Arrays.copyOf(cachedVertices, cachedVertices.length);
    }
    
    /**
     * Helper method to rotate a point using Euler angles.
     * 
     * <p>Applies rotations in order: X (pitch), Y (yaw), Z (roll)
     * This is an implementation detail demonstrating <b>Encapsulation</b>.
     * 
     * @param x local x coordinate
     * @param y local y coordinate
     * @param z local z coordinate
     * @return rotated coordinates as [x, y, z]
     */
    private double[] rotatePoint(double x, double y, double z) {
        // Rotation matrices applied in sequence
        
        // Rotate around X-axis
        double cosX = Math.cos(rotationX);
        double sinX = Math.sin(rotationX);
        double y1 = y * cosX - z * sinX;
        double z1 = y * sinX + z * cosX;
        
        // Rotate around Y-axis
        double cosY = Math.cos(rotationY);
        double sinY = Math.sin(rotationY);
        double x2 = x * cosY + z1 * sinY;
        double z2 = -x * sinY + z1 * cosY;
        
        // Rotate around Z-axis
        double cosZ = Math.cos(rotationZ);
        double sinZ = Math.sin(rotationZ);
        double x3 = x2 * cosZ - y1 * sinZ;
        double y3 = x2 * sinZ + y1 * cosZ;
        
        return new double[]{x3, y3, z2};
    }
    
    // ==================== EDGE OPERATIONS ====================
    
    /**
     * Returns all 12 edges of the cube as Line3D objects.
     * 
     * <p>Demonstrates the <b>Composite Pattern</b> where cube is composed of Line3D objects.
     * Edges connect vertices according to cube topology.
     * 
     * <p>Edge grouping:
     * <ul>
     *   <li>Edges 0-3: Bottom face</li>
     *   <li>Edges 4-7: Top face</li>
     *   <li>Edges 8-11: Vertical edges</li>
     * </ul>
     * 
     * @return array of 12 edges
     */
    public Line3D[] getEdges() {
        logger.log(Level.FINE, "Calculating edges");
        
        if (cachedEdges == null) {
            logger.log(Level.FINE, "Edges not cached, computing...");
            
            Point3D[] vertices = getVertices();
            Line3D[] edges = new Line3D[12];
            
            // Bottom face edges (0-3)
            edges[0] = new Line3D(vertices[0], vertices[1]);
            edges[1] = new Line3D(vertices[1], vertices[2]);
            edges[2] = new Line3D(vertices[2], vertices[3]);
            edges[3] = new Line3D(vertices[3], vertices[0]);
            
            // Top face edges (4-7)
            edges[4] = new Line3D(vertices[4], vertices[5]);
            edges[5] = new Line3D(vertices[5], vertices[6]);
            edges[6] = new Line3D(vertices[6], vertices[7]);
            edges[7] = new Line3D(vertices[7], vertices[4]);
            
            // Vertical edges (8-11)
            edges[8] = new Line3D(vertices[0], vertices[4]);
            edges[9] = new Line3D(vertices[1], vertices[5]);
            edges[10] = new Line3D(vertices[2], vertices[6]);
            edges[11] = new Line3D(vertices[3], vertices[7]);
            
            cachedEdges = edges;
            logger.log(Level.INFO, "Computed cube edges");
        } else {
            logger.log(Level.FINE, "Using cached edges");
        }
        
        // Return defensive copy
        return Arrays.copyOf(cachedEdges, cachedEdges.length);
    }
    
    // ==================== TRANSFORMATION METHODS ====================
    
    /**
     * Translates the cube by the specified offset.
     * 
     * <p>Demonstrates <b>Immutability Pattern</b> by returning a new Cube3D instance
     * rather than modifying the current one. This follows functional programming
     * principles and ensures thread safety.
     * 
     * @param dx translation along x-axis
     * @param dy translation along y-axis
     * @param dz translation along z-axis
     * @return a new translated Cube3D
     */
    public Cube3D translate(double dx, double dy, double dz) {
        logger.log(Level.INFO, "Translating cube by ({0}, {1}, {2})", new Object[]{dx, dy, dz});
        
        Point3D newCenter = new Point3D(
            center.getX() + dx,
            center.getY() + dy,
            center.getZ() + dz
        );
        
        return new Cube3D(newCenter, sideLength, rotationX, rotationY, rotationZ);
    }
    
    /**
     * Translates the cube by a vector.
     * 
     * @param offset the translation vector
     * @return a new translated Cube3D
     */
    public Cube3D translate(Point3D offset) {
        if (offset == null) {
            throw new IllegalArgumentException("Offset cannot be null");
        }
        return translate(offset.getX(), offset.getY(), offset.getZ());
    }
    
    /**
     * Rotates the cube around the X-axis by the specified angle.
     * 
     * <p>Returns a new Cube3D with updated rotation, maintaining immutability.
     * 
     * @param angle rotation angle in radians
     * @return a new rotated Cube3D
     */
    public Cube3D rotateX(double angle) {
        logger.log(Level.INFO, "Rotating cube around X-axis by {0} radians", angle);
        return new Cube3D(center, sideLength, rotationX + angle, rotationY, rotationZ);
    }
    
    /**
     * Rotates the cube around the Y-axis by the specified angle.
     * 
     * @param angle rotation angle in radians
     * @return a new rotated Cube3D
     */
    public Cube3D rotateY(double angle) {
        logger.log(Level.INFO, "Rotating cube around Y-axis by {0} radians", angle);
        return new Cube3D(center, sideLength, rotationX, rotationY + angle, rotationZ);
    }
    
    /**
     * Rotates the cube around the Z-axis by the specified angle.
     * 
     * @param angle rotation angle in radians
     * @return a new rotated Cube3D
     */
    public Cube3D rotateZ(double angle) {
        logger.log(Level.INFO, "Rotating cube around Z-axis by {0} radians", angle);
        return new Cube3D(center, sideLength, rotationX, rotationY, rotationZ + angle);
    }
    
    /**
     * Rotates the cube around all three axes.
     * 
     * @param angleX rotation around X-axis in radians
     * @param angleY rotation around Y-axis in radians
     * @param angleZ rotation around Z-axis in radians
     * @return a new rotated Cube3D
     */
    public Cube3D rotate(double angleX, double angleY, double angleZ) {
        logger.log(Level.INFO, "Rotating cube by ({0}, {1}, {2}) radians", 
                  new Object[]{angleX, angleY, angleZ});
        return new Cube3D(center, sideLength, 
                         rotationX + angleX, 
                         rotationY + angleY, 
                         rotationZ + angleZ);
    }
    
    /**
     * Scales the cube by the specified factor.
     * 
     * <p>Returns a new Cube3D with scaled dimensions.
     * 
     * @param scaleFactor the scaling factor (must be positive)
     * @return a new scaled Cube3D
     * @throws IllegalArgumentException if scale factor is non-positive
     */
    public Cube3D scale(double scaleFactor) {
        logger.log(Level.INFO, "Scaling cube by factor {0}", scaleFactor);
        
        if (scaleFactor <= 0) {
            logger.log(Level.SEVERE, "Invalid scale factor: {0}", scaleFactor);
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        
        return new Cube3D(center, sideLength * scaleFactor, rotationX, rotationY, rotationZ);
    }
    
    // ==================== SPATIAL QUERIES ====================
    
    /**
     * Checks if a point is inside the cube.
     * 
     * <p>Uses axis-aligned bounding box test after inverse rotation transformation.
     * This demonstrates algorithmic problem-solving in geometric contexts.
     * 
     * @param point the point to test
     * @return true if the point is inside or on the cube boundary
     * @throws IllegalArgumentException if point is null
     */
    public boolean contains(Point3D point) {
        logger.log(Level.FINE, "Checking if cube contains point {0}", point);
        
        if (point == null) {
            logger.log(Level.SEVERE, "Cannot check containment for null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        // Transform point to cube's local coordinate system
        double localX = point.getX() - center.getX();
        double localY = point.getY() - center.getY();
        double localZ = point.getZ() - center.getZ();
        
        // Apply inverse rotation
        double[] unrotated = inverseRotatePoint(localX, localY, localZ);
        
        // Check if within cube bounds
        double h = sideLength / 2.0;
        boolean inside = Math.abs(unrotated[0]) <= h && 
                        Math.abs(unrotated[1]) <= h && 
                        Math.abs(unrotated[2]) <= h;
        
        logger.log(Level.INFO, "Point {0} {1} inside cube", 
                  new Object[]{point, inside ? "is" : "is not"});
        
        return inside;
    }
    
    /**
     * Helper method for inverse rotation transformation.
     * 
     * <p>Applies rotations in reverse order: Z, Y, X with negative angles.
     * 
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return unrotated coordinates
     */
    private double[] inverseRotatePoint(double x, double y, double z) {
        // Inverse Z rotation
        double cosZ = Math.cos(-rotationZ);
        double sinZ = Math.sin(-rotationZ);
        double x1 = x * cosZ - y * sinZ;
        double y1 = x * sinZ + y * cosZ;
        
        // Inverse Y rotation
        double cosY = Math.cos(-rotationY);
        double sinY = Math.sin(-rotationY);
        double x2 = x1 * cosY + z * sinY;
        double z2 = -x1 * sinY + z * cosY;
        
        // Inverse X rotation
        double cosX = Math.cos(-rotationX);
        double sinX = Math.sin(-rotationX);
        double y3 = y1 * cosX - z2 * sinX;
        double z3 = y1 * sinX + z2 * cosX;
        
        return new double[]{x2, y3, z3};
    }
    
    /**
     * Calculates the distance from the cube's center to a point.
     * 
     * @param point the target point
     * @return the distance
     * @throws IllegalArgumentException if point is null
     */
    public double distanceToPoint(Point3D point) {
        logger.log(Level.FINE, "Calculating distance to point {0}", point);
        
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        double dx = point.getX() - center.getX();
        double dy = point.getY() - center.getY();
        double dz = point.getZ() - center.getZ();
        
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Checks if this cube intersects with another cube.
     * 
     * <p>Simplified intersection test using bounding spheres for efficiency.
     * For precise intersection, would need SAT (Separating Axis Theorem).
     * 
     * @param other the other cube
     * @return true if cubes intersect
     * @throws IllegalArgumentException if other is null
     */
    public boolean intersects(Cube3D other) {
        logger.log(Level.INFO, "Checking intersection with another cube");
        
        if (other == null) {
            logger.log(Level.SEVERE, "Cannot check intersection with null cube");
            throw new IllegalArgumentException("Other cube cannot be null");
        }
        
        // Use bounding sphere test (conservative but fast)
        double distance = this.center.distanceTo(other.center);
        double radiusSum = (this.getDiagonal() + other.getDiagonal()) / 2.0;
        
        boolean intersects = distance < radiusSum;
        
        logger.log(Level.INFO, "Cubes {0} intersect", intersects ? "do" : "do not");
        
        return intersects;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Returns the minimum corner of the axis-aligned bounding box.
     * 
     * @return the minimum corner point
     */
    public Point3D getMinCorner() {
        Point3D[] vertices = getVertices();
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        
        for (Point3D vertex : vertices) {
            minX = Math.min(minX, vertex.getX());
            minY = Math.min(minY, vertex.getY());
            minZ = Math.min(minZ, vertex.getZ());
        }
        
        return new Point3D(minX, minY, minZ);
    }
    
    /**
     * Returns the maximum corner of the axis-aligned bounding box.
     * 
     * @return the maximum corner point
     */
    public Point3D getMaxCorner() {
        Point3D[] vertices = getVertices();
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        
        for (Point3D vertex : vertices) {
            maxX = Math.max(maxX, vertex.getX());
            maxY = Math.max(maxY, vertex.getY());
            maxZ = Math.max(maxZ, vertex.getZ());
        }
        
        return new Point3D(maxX, maxY, maxZ);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Cube3D other = (Cube3D) obj;
        
        return Double.compare(other.sideLength, sideLength) == 0 &&
               Double.compare(other.rotationX, rotationX) == 0 &&
               Double.compare(other.rotationY, rotationY) == 0 &&
               Double.compare(other.rotationZ, rotationZ) == 0 &&
               center.equals(other.center);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + center.hashCode();
        result = 31 * result + Double.hashCode(sideLength);
        result = 31 * result + Double.hashCode(rotationX);
        result = 31 * result + Double.hashCode(rotationY);
        result = 31 * result + Double.hashCode(rotationZ);
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("Cube3D[center=%s, side=%.4f, rotation=(%.2f, %.2f, %.2f), volume=%.4f]",
                           center, sideLength, rotationX, rotationY, rotationZ, getVolume());
    }
}