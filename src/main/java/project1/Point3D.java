package project1;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;



    /**
     * Represents a point in 3D space with x, y, and z coordinates.
     *
     * This class demonstrates several object-oriented design patterns:
     *
     * 1. VALUE OBJECT PATTERN: This class is immutable after construction, making it thread-safe
     *    and preventing unintended modifications. All fields are final, and methods that would
     *    "modify" the point instead return new Point3D instances.
     *
     * 2. ENCAPSULATION: The internal state (x, y, z coordinates) is kept private with controlled
     *    access through getter methods, following the principle of information hiding.
     *
     * 3. BUILDER PATTERN (simplified): While not a full builder, the static factory method
     *    provides a clear API for object construction.
     *
     * These patterns demonstrate foundational principles for data structures:
     * - Immutability ensures data integrity and simplifies reasoning about program state
     * - Encapsulation protects invariants and provides a stable public interface
     * - Value semantics (via equals/hashCode) allow points to be used in collections
     *
     * @author Spring Framework Style Documentation
     * @version 1.0
     */
    public class Point3D {

        private static final Logger logger = Logger.getLogger(Point3D.class.getName());

        // Immutable fields representing the 3D coordinates
        private final double x;
        private final double y;
        private final double z;

        /**
         * Constructs a new Point3D with the specified coordinates.
         *
         * This constructor demonstrates the principle of COMPLETE INITIALIZATION:
         * All required data is provided at construction time, ensuring the object
         * is never in an invalid or incomplete state.
         *
         * @param x the x-coordinate in 3D space
         * @param y the y-coordinate in 3D space
         * @param z the z-coordinate in 3D space
         * @throws IllegalArgumentException if any coordinate is NaN or infinite
         */
        public Point3D(double x, double y, double z) {
            logger.info(String.format("Creating Point3D with coordinates: x=%.2f, y=%.2f, z=%.2f", x, y, z));

            // Validate input to maintain class invariants
            if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
                logger.severe("Attempted to create Point3D with NaN coordinate(s)");
                throw new IllegalArgumentException("Coordinates cannot be NaN");
            }

            if (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) {
                logger.severe("Attempted to create Point3D with infinite coordinate(s)");
                throw new IllegalArgumentException("Coordinates cannot be infinite");
            }

            this.x = x;
            this.y = y;
            this.z = z;

            logger.fine(String.format("Successfully created Point3D: %s", this));
        }

        /**
         * Static factory method for creating a Point3D at the origin (0, 0, 0).
         *
         * This demonstrates the FACTORY METHOD PATTERN, providing a named constructor
         * that makes the intent clear and can be optimized to return cached instances.
         * Factory methods are preferred over constructors when:
         * - The name adds clarity (origin() is clearer than new Point3D(0,0,0))
         * - Instance control is needed (could cache the origin instance)
         * - The return type needs flexibility (could return subclasses)
         *
         * @return a Point3D representing the origin of the coordinate system
         */
        public static Point3D origin() {
            logger.info("Creating Point3D at origin");
            return new Point3D(0.0, 0.0, 0.0);
        }

        /**
         * Returns the x-coordinate of this point.
         *
         * This getter method provides READ-ONLY ACCESS to internal state,
         * maintaining encapsulation while allowing clients to query the object's state.
         *
         * @return the x-coordinate value
         */
        public double getX() {
            logger.fine(String.format("Getting x-coordinate: %.2f", x));
            return x;
        }

        /**
         * Returns the y-coordinate of this point.
         *
         * @return the y-coordinate value
         */
        public double getY() {
            logger.fine(String.format("Getting y-coordinate: %.2f", y));
            return y;
        }

        /**
         * Returns the z-coordinate of this point.
         *
         * @return the z-coordinate value
         */
        public double getZ() {
            logger.fine(String.format("Getting z-coordinate: %.2f", z));
            return z;
        }

        /**
         * Calculates the Euclidean distance from this point to another point.
         *
         * This method demonstrates the ALGORITHM principle of DECOMPOSITION:
         * Complex calculations are broken down into clear, manageable steps.
         * The distance formula √((x₂-x₁)² + (y₂-y₁)² + (z₂-z₁)²) is implemented
         * step-by-step for clarity and maintainability.
         *
         * Time Complexity: O(1) - constant time operation
         * Space Complexity: O(1) - uses only a fixed amount of extra space
         *
         * @param other the point to calculate distance to
         * @return the Euclidean distance between this point and the other point
         * @throws NullPointerException if other is null
         */
        public double distanceTo(Point3D other) {
            logger.info(String.format("Calculating distance from %s to %s", this, other));

            if (other == null) {
                logger.severe("Attempted to calculate distance to null point");
                throw new NullPointerException("Cannot calculate distance to null point");
            }

            // Calculate differences in each dimension
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            double dz = this.z - other.z;

            logger.fine(String.format("Differences: dx=%.2f, dy=%.2f, dz=%.2f", dx, dy, dz));

            // Apply the distance formula
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            logger.info(String.format("Calculated distance: %.2f", distance));
            return distance;
        }

        /**
         * Calculates the Manhattan distance (L1 norm) from this point to another point.
         *
         * Manhattan distance is the sum of absolute differences in each dimension:
         * |x₂-x₁| + |y₂-y₁| + |z₂-z₁|
         *
         * This metric is useful in grid-based systems and is computationally cheaper
         * than Euclidean distance as it avoids square root calculation.
         *
         * @param other the point to calculate Manhattan distance to
         * @return the Manhattan distance between this point and the other point
         * @throws NullPointerException if other is null
         */
        public double manhattanDistanceTo(Point3D other) {
            logger.info(String.format("Calculating Manhattan distance from %s to %s", this, other));

            if (other == null) {
                logger.severe("Attempted to calculate Manhattan distance to null point");
                throw new NullPointerException("Cannot calculate Manhattan distance to null point");
            }

            double distance = Math.abs(this.x - other.x) +
                    Math.abs(this.y - other.y) +
                    Math.abs(this.z - other.z);

            logger.info(String.format("Calculated Manhattan distance: %.2f", distance));
            return distance;
        }

        /**
         * Calculates the magnitude (length) of the vector from origin to this point.
         *
         * This demonstrates the concept of treating a point as a POSITION VECTOR.
         * The magnitude represents the distance from the origin to this point.
         *
         * Formula: √(x² + y² + z²)
         *
         * @return the magnitude of this point as a vector from the origin
         */
        public double magnitude() {
            logger.info(String.format("Calculating magnitude of %s", this));

            double mag = Math.sqrt(x * x + y * y + z * z);

            logger.info(String.format("Calculated magnitude: %.2f", mag));
            return mag;
        }

        /**
         * Returns a new Point3D that is the result of adding another point to this point.
         *
         * This method demonstrates VECTOR ADDITION and the IMMUTABILITY pattern:
         * Instead of modifying the current object, we return a new Point3D.
         * This preserves the original object and prevents side effects.
         *
         * Operation: (x₁ + x₂, y₁ + y₂, z₁ + z₂)
         *
         * @param other the point to add to this point
         * @return a new Point3D representing the sum of the two points
         * @throws NullPointerException if other is null
         */
        public Point3D add(Point3D other) {
            logger.info(String.format("Adding %s to %s", other, this));

            if (other == null) {
                logger.severe("Attempted to add null point");
                throw new NullPointerException("Cannot add null point");
            }

            Point3D result = new Point3D(this.x + other.x, this.y + other.y, this.z + other.z);

            logger.info(String.format("Addition result: %s", result));
            return result;
        }

        /**
         * Returns a new Point3D that is the result of subtracting another point from this point.
         *
         * Vector subtraction creates a displacement vector from the other point to this point.
         * This is fundamental in calculating directions and relative positions.
         *
         * Operation: (x₁ - x₂, y₁ - y₂, z₁ - z₂)
         *
         * @param other the point to subtract from this point
         * @return a new Point3D representing the difference of the two points
         * @throws NullPointerException if other is null
         */
        public Point3D subtract(Point3D other) {
            logger.info(String.format("Subtracting %s from %s", other, this));

            if (other == null) {
                logger.severe("Attempted to subtract null point");
                throw new NullPointerException("Cannot subtract null point");
            }

            Point3D result = new Point3D(this.x - other.x, this.y - other.y, this.z - other.z);

            logger.info(String.format("Subtraction result: %s", result));
            return result;
        }

        /**
         * Returns a new Point3D that is this point scaled by a scalar value.
         *
         * Scalar multiplication scales the vector uniformly in all directions.
         * This is essential for vector normalization, scaling transformations,
         * and interpolation algorithms.
         *
         * Operation: (scalar × x, scalar × y, scalar × z)
         *
         * @param scalar the value to multiply each coordinate by
         * @return a new Point3D with all coordinates multiplied by the scalar
         */
        public Point3D scale(double scalar) {
            logger.info(String.format("Scaling %s by %.2f", this, scalar));

            if (Double.isNaN(scalar) || Double.isInfinite(scalar)) {
                logger.warning("Attempted to scale by NaN or infinite scalar");
                throw new IllegalArgumentException("Scalar cannot be NaN or infinite");
            }

            Point3D result = new Point3D(this.x * scalar, this.y * scalar, this.z * scalar);

            logger.info(String.format("Scaling result: %s", result));
            return result;
        }

        /**
         * Returns a normalized version of this point (unit vector in the same direction).
         *
         * Normalization is crucial in computer graphics and physics simulations.
         * A normalized vector has magnitude 1 while preserving direction.
         * This is the foundation for many algorithms including lighting calculations
         * and direction-based computations.
         *
         * Mathematical basis: v̂ = v / ||v||
         *
         * @return a new Point3D representing the unit vector in the direction of this point
         * @throws ArithmeticException if this point is at the origin (magnitude is zero)
         */
        public Point3D normalize() {
            logger.info(String.format("Normalizing %s", this));

            double mag = magnitude();

            if (mag == 0.0) {
                logger.severe("Attempted to normalize zero-magnitude vector");
                throw new ArithmeticException("Cannot normalize a zero-magnitude vector");
            }

            Point3D result = scale(1.0 / mag);

            logger.info(String.format("Normalized result: %s with magnitude %.4f", result, result.magnitude()));
            return result;
        }

        /**
         * Calculates the dot product of this point with another point (treating them as vectors).
         *
         * The dot product is a fundamental operation in linear algebra with geometric interpretation:
         * - Returns the projection of one vector onto another, scaled by magnitudes
         * - Result is positive if vectors point in similar directions
         * - Result is zero if vectors are perpendicular (orthogonal)
         * - Result is negative if vectors point in opposite directions
         *
         * Formula: a · b = a₁b₁ + a₂b₂ + a₃b₃
         * Geometric: a · b = ||a|| ||b|| cos(θ)
         *
         * Time Complexity: O(1)
         *
         * @param other the point to calculate dot product with
         * @return the dot product of this point and the other point
         * @throws NullPointerException if other is null
         */
        public double dot(Point3D other) {
            logger.info(String.format("Calculating dot product of %s and %s", this, other));

            if (other == null) {
                logger.severe("Attempted to calculate dot product with null point");
                throw new NullPointerException("Cannot calculate dot product with null point");
            }

            double result = this.x * other.x + this.y * other.y + this.z * other.z;

            logger.info(String.format("Dot product result: %.2f", result));
            return result;
        }

        /**
         * Calculates the cross product of this point with another point (treating them as vectors).
         *
         * The cross product produces a vector perpendicular to both input vectors.
         * This is essential for:
         * - Calculating surface normals in 3D graphics
         * - Determining rotation axes
         * - Computing areas of parallelograms
         * - Testing if vectors are parallel (cross product is zero)
         *
         * Formula: a × b = (a₂b₃ - a₃b₂, a₃b₁ - a₁b₃, a₁b₂ - a₂b₁)
         *
         * Properties:
         * - Anti-commutative: a × b = -(b × a)
         * - Magnitude equals area of parallelogram formed by vectors
         * - Direction follows right-hand rule
         *
         * @param other the point to calculate cross product with
         * @return a new Point3D representing the cross product vector
         * @throws NullPointerException if other is null
         */
        public Point3D cross(Point3D other) {
            logger.info(String.format("Calculating cross product of %s and %s", this, other));

            if (other == null) {
                logger.severe("Attempted to calculate cross product with null point");
                throw new NullPointerException("Cannot calculate cross product with null point");
            }

            double newX = this.y * other.z - this.z * other.y;
            double newY = this.z * other.x - this.x * other.z;
            double newZ = this.x * other.y - this.y * other.x;

            Point3D result = new Point3D(newX, newY, newZ);

            logger.info(String.format("Cross product result: %s", result));
            return result;
        }

        /**
         * Rotates this point around the X-axis by the specified angle.
         *
         * This demonstrates ROTATION MATRICES, a fundamental concept in computer graphics
         * and robotics. The rotation is performed using the 3D rotation matrix for X-axis:
         *
         * Rₓ(θ) = [1    0         0    ]
         *         [0  cos(θ)  -sin(θ)]
         *         [0  sin(θ)   cos(θ)]
         *
         * The rotation follows the right-hand rule: positive angles rotate counterclockwise
         * when looking along the positive X-axis toward the origin.
         *
         * @param angleRadians the angle to rotate by, in radians
         * @return a new Point3D representing this point rotated around the X-axis
         */
        public Point3D rotateX(double angleRadians) {
            logger.info(String.format("Rotating %s around X-axis by %.2f radians", this, angleRadians));

            double cos = Math.cos(angleRadians);
            double sin = Math.sin(angleRadians);

            double newX = this.x;
            double newY = this.y * cos - this.z * sin;
            double newZ = this.y * sin + this.z * cos;

            Point3D result = new Point3D(newX, newY, newZ);

            logger.info(String.format("Rotation result: %s", result));
            return result;
        }

        /**
         * Rotates this point around the Y-axis by the specified angle.
         *
         * Rotation matrix for Y-axis:
         *
         * Rᵧ(θ) = [ cos(θ)  0  sin(θ)]
         *         [   0     1    0   ]
         *         [-sin(θ)  0  cos(θ)]
         *
         * @param angleRadians the angle to rotate by, in radians
         * @return a new Point3D representing this point rotated around the Y-axis
         */
        public Point3D rotateY(double angleRadians) {
            logger.info(String.format("Rotating %s around Y-axis by %.2f radians", this, angleRadians));

            double cos = Math.cos(angleRadians);
            double sin = Math.sin(angleRadians);

            double newX = this.x * cos + this.z * sin;
            double newY = this.y;
            double newZ = -this.x * sin + this.z * cos;

            Point3D result = new Point3D(newX, newY, newZ);

            logger.info(String.format("Rotation result: %s", result));
            return result;
        }

        /**
         * Rotates this point around the Z-axis by the specified angle.
         *
         * Rotation matrix for Z-axis:
         *
         * Rᵤ(θ) = [cos(θ)  -sin(θ)  0]
         *         [sin(θ)   cos(θ)  0]
         *         [  0        0     1]
         *
         * @param angleRadians the angle to rotate by, in radians
         * @return a new Point3D representing this point rotated around the Z-axis
         */
        public Point3D rotateZ(double angleRadians) {
            logger.info(String.format("Rotating %s around Z-axis by %.2f radians", this, angleRadians));

            double cos = Math.cos(angleRadians);
            double sin = Math.sin(angleRadians);

            double newX = this.x * cos - this.y * sin;
            double newY = this.x * sin + this.y * cos;
            double newZ = this.z;

            Point3D result = new Point3D(newX, newY, newZ);

            logger.info(String.format("Rotation result: %s", result));
            return result;
        }

        /**
         * Calculates linear interpolation between this point and another point.
         *
         * Interpolation (lerp) is essential for:
         * - Animation and smooth transitions
         * - Path generation
         * - Color blending in graphics
         * - Numerical approximation
         *
         * Formula: lerp(a, b, t) = a + t(b - a) = (1-t)a + tb
         *
         * The parameter t determines the position along the line segment:
         * - t = 0.0 returns this point
         * - t = 1.0 returns the other point
         * - t = 0.5 returns the midpoint
         * - t can be outside [0,1] for extrapolation
         *
         * @param other the target point to interpolate toward
         * @param t the interpolation parameter (0.0 to 1.0 for interpolation)
         * @return a new Point3D representing the interpolated position
         * @throws NullPointerException if other is null
         */
        public Point3D lerp(Point3D other, double t) {
            logger.info(String.format("Interpolating from %s to %s with t=%.2f", this, other, t));

            if (other == null) {
                logger.severe("Attempted to interpolate with null point");
                throw new NullPointerException("Cannot interpolate with null point");
            }

            if (t < 0.0 || t > 1.0) {
                logger.warning(String.format("Interpolation parameter t=%.2f is outside [0,1] range", t));
            }

            double newX = this.x + t * (other.x - this.x);
            double newY = this.y + t * (other.y - this.y);
            double newZ = this.z + t * (other.z - this.z);

            Point3D result = new Point3D(newX, newY, newZ);

            logger.info(String.format("Interpolation result: %s", result));
            return result;
        }

        /**
         * Determines whether this point is equal to another object.
         *
         * This method demonstrates proper implementation of the EQUALITY CONTRACT,
         * which is critical for:
         * - Using objects as keys in HashMap and HashSet
         * - Ensuring consistent behavior in collections
         * - Implementing value semantics
         *
         * The equality contract requires:
         * 1. Reflexive: x.equals(x) is true
         * 2. Symmetric: x.equals(y) ⟺ y.equals(x)
         * 3. Transitive: if x.equals(y) and y.equals(z), then x.equals(z)
         * 4. Consistent: multiple calls return the same result
         * 5. x.equals(null) returns false
         *
         * We use Double.compare() rather than == to handle NaN and -0.0 correctly.
         *
         * @param obj the object to compare with this point
         * @return true if the object is a Point3D with equal coordinates
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                logger.fine("Equality check: same object reference");
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                logger.fine("Equality check: null or different class");
                return false;
            }

            Point3D other = (Point3D) obj;
            boolean isEqual = Double.compare(this.x, other.x) == 0 &&
                    Double.compare(this.y, other.y) == 0 &&
                    Double.compare(this.z, other.z) == 0;

            logger.fine(String.format("Equality check: %s equals %s = %b", this, other, isEqual));
            return isEqual;
        }

        /**
         * Returns a hash code value for this point.
         *
         * This demonstrates proper HASH CODE implementation, which must satisfy:
         * - If a.equals(b), then a.hashCode() == b.hashCode()
         * - Hash codes should be well-distributed to minimize collisions
         * - Hash codes should be consistent with equals()
         *
         * We use Objects.hash() which handles the primitive double values correctly
         * and provides good distribution. This is essential for performance when
         * using Point3D in hash-based collections.
         *
         * The hash function's quality affects:
         * - HashMap/HashSet performance (O(1) vs O(n) operations)
         * - Memory usage in hash tables
         * - Overall application performance when used in collections
         *
         * @return a hash code value for this point
         */
        @Override
        public int hashCode() {
            int hash = Objects.hash(x, y, z);
            logger.fine(String.format("Generated hash code for %s: %d", this, hash));
            return hash;
        }

        /**
         * Returns a string representation of this point.
         *
         * This demonstrates the CONTRACT OF toString(), which should:
         * - Provide useful information for debugging
         * - Be concise and readable
         * - Include the class name for clarity
         * - Format numeric values appropriately
         *
         * The format used here (Point3D[x=1.00, y=2.00, z=3.00]) is:
         * - Self-documenting (shows class and field names)
         * - Consistent with Java conventions
         * - Useful in logging and debugging contexts
         *
         * @return a string representation of this point
         */
        @Override
        public String toString() {
            return String.format("Point3D[x=%.2f, y=%.2f, z=%.2f]", x, y, z);
        }

        /**
         * Demonstrates usage of the Point3D class with various operations.
         *
         * This main method serves as:
         * - Example code for class usage
         * - Basic integration test
         * - Documentation of expected behavior
         *
         * @param args command line arguments (not used)
         */
        public static void main(String[] args) {
            // Configure logging to show INFO and above
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.INFO);

            logger.info("=== Point3D Demonstration ===");

            try {
                // Create points
                Point3D p1 = new Point3D(1.0, 2.0, 3.0);
                Point3D p2 = new Point3D(4.0, 5.0, 6.0);
                Point3D origin = Point3D.origin();

                // Distance calculations
                double distance = p1.distanceTo(p2);
                System.out.println("Distance between p1 and p2: " + distance);

                // Vector operations
                Point3D sum = p1.add(p2);
                System.out.println("Sum: " + sum);

                Point3D difference = p2.subtract(p1);
                System.out.println("Difference: " + difference);

                Point3D scaled = p1.scale(2.0);
                System.out.println("Scaled: " + scaled);

                // Magnitude and normalization
                double mag = p1.magnitude();
                System.out.println("Magnitude of p1: " + mag);

                Point3D normalized = p1.normalize();
                System.out.println("Normalized p1: " + normalized);
                System.out.println("Normalized magnitude: " + normalized.magnitude());

                // Dot and cross products
                double dotProduct = p1.dot(p2);
                System.out.println("Dot product: " + dotProduct);

                Point3D crossProduct = p1.cross(p2);
                System.out.println("Cross product: " + crossProduct);

                // Rotation
                Point3D rotated = p1.rotateZ(Math.PI / 2); // 90 degrees around Z-axis
                System.out.println("Rotated 90° around Z-axis: " + rotated);

                // Interpolation
                Point3D midpoint = p1.lerp(p2, 0.5);
                System.out.println("Midpoint: " + midpoint);

                logger.info("=== Demonstration Complete ===");

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during demonstration", e);
            }
        }
    }

