package skymc.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for generating block patterns in Minecraft
 */
public class PatternGenerator {

    /**
     * Enum for the different types of patterns that can be generated
     */
    public enum PatternType {
        LINEAR_HORIZONTAL("Linear (Horizontal)"),
        LINEAR_VERTICAL("Linear (Vertical)"),
        RADIAL("Radial (Circle)"),
        DIAGONAL("Diagonal"),
        SPIRAL("Spiral"),
        PERLIN("Perlin"),
        VORONOI("Voronoi"),
        CELLULAR("Cellular");

        private final String displayName;

        PatternType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Enum for the different curve types that can be applied to patterns
     */
    public enum CurveType {
        LINEAR("Linear (Even)"),
        EASE_IN("Ease In (Slow Start)"),
        EASE_OUT("Ease Out (Slow End)"),
        EASE_IN_OUT("Ease In-Out (Smooth)");

        private final String displayName;

        CurveType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Generates a block pattern based on the provided parameters
     *
     * @param startBlock The block to start the pattern with
     * @param endBlock The block to end the pattern with
     * @param size The size of the pattern (width/height)
     * @param noiseLevel The amount of noise to add (0-1)
     * @param patternType The type of pattern to generate
     * @param curveType The type of curve to apply to the pattern
     * @param useTopTexture Whether to use the top texture or side texture
     * @return A 2D array of Block objects representing the generated pattern
     */
    public Block[][] generatePattern(Block startBlock, Block endBlock, int size,
                                  double noiseLevel, PatternType patternType, CurveType curveType, boolean useTopTexture) {
        Block[][] pattern = new Block[size][size];

        switch (patternType) {
            case LINEAR_HORIZONTAL:
                generateLinearHorizontalPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
            case LINEAR_VERTICAL:
                generateLinearVerticalPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
            case RADIAL:
                generateRadialPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
            case DIAGONAL:
                generateDiagonalPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
            case SPIRAL:
                generateSpiralPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
            case PERLIN:
                generatePerlinPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
            case VORONOI:
                generateVoronoiPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
            case CELLULAR:
                generateCellularPattern(pattern, startBlock, endBlock, size, noiseLevel, curveType, useTopTexture);
                break;
        }

        return pattern;
    }

    /**
     * Generates a horizontal linear pattern
     */
    private void generateLinearHorizontalPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                            int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double t = (double) x / (size - 1);
                double adjustedT = applyEasingCurve(t, curveType);

                // Apply noise if needed
                if (noiseLevel > 0) {
                    double noise = (Math.random() - 0.5) * 2 * noiseLevel;
                    adjustedT = Math.max(0, Math.min(1, adjustedT + noise));
                }

                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, adjustedT, useTopTexture);
            }
        }
    }

    /**
     * Generates a vertical linear pattern
     */
    private void generateLinearVerticalPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                          int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        for (int y = 0; y < size; y++) {
            double t = (double) y / (size - 1);
            double adjustedT = applyEasingCurve(t, curveType);

            for (int x = 0; x < size; x++) {
                // Apply noise if needed
                double finalT = adjustedT;
                if (noiseLevel > 0) {
                    double noise = (Math.random() - 0.5) * 2 * noiseLevel;
                    finalT = Math.max(0, Math.min(1, finalT + noise));
                }

                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, finalT, useTopTexture);
            }
        }
    }

    /**
     * Generates a radial pattern
     */
    private void generateRadialPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                   int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        int center = size / 2;
        double maxDistance = center; // Use center as max distance

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Calculate distance from center
                double dx = x - center;
                double dy = y - center;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Convert to t parameter (0 to 1)
                double t = Math.min(1.0, distance / maxDistance);
                double adjustedT = applyEasingCurve(t, curveType);

                // Apply noise if needed
                if (noiseLevel > 0) {
                    double noise = (Math.random() - 0.5) * 2 * noiseLevel;
                    adjustedT = Math.max(0, Math.min(1, adjustedT + noise));
                }

                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, adjustedT, useTopTexture);
            }
        }
    }

    /**
     * Generates a diagonal pattern
     */
    private void generateDiagonalPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                     int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Calculate position on diagonal (0 to 1)
                double t = (x + y) / (double) (2 * (size - 1));
                double adjustedT = applyEasingCurve(t, curveType);

                // Apply noise if needed
                if (noiseLevel > 0) {
                    double noise = (Math.random() - 0.5) * 2 * noiseLevel;
                    adjustedT = Math.max(0, Math.min(1, adjustedT + noise));
                }

                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, adjustedT, useTopTexture);
            }
        }
    }

    /**
     * Generates a spiral pattern
     */
    private void generateSpiralPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                   int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        int center = size / 2;
        double maxDistance = Math.sqrt(2) * center; // Max possible distance in the grid

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Calculate angle and distance
                double dx = x - center;
                double dy = y - center;
                double angle = Math.atan2(dy, dx);
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Create spiral parameter (combination of angle and distance)
                double t = ((distance / maxDistance) + (angle + Math.PI) / (2 * Math.PI)) % 1.0;
                double adjustedT = applyEasingCurve(t, curveType);

                // Apply noise if needed
                if (noiseLevel > 0) {
                    double noise = (Math.random() - 0.5) * 2 * noiseLevel;
                    adjustedT = Math.max(0, Math.min(1, adjustedT + noise));
                }

                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, adjustedT, useTopTexture);
            }
        }
    }

    /**
     * Generates a proper Perlin noise pattern with improved quality
     * while maintaining compatibility with the original implementation
     */
    private void generatePerlinPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                    int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        // Seed for consistent noise generation
        long seed = System.currentTimeMillis();
        
        // Parameters for improved Perlin noise
        int octaves = 4;          // Number of noise layers to combine
        double persistence = 0.5; // How much each octave contributes
        double scale = 0.1;       // Base scale factor
        
        // Generate noise using proper frequency and amplitude scaling
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Generate coherent noise value using multiple octaves
                double noise = 0;
                double amplitude = 1.0;
                double frequency = 1.0;
                double maxValue = 0;
                
                // Sum multiple octaves of noise
                for (int i = 0; i < octaves; i++) {
                    // Use consistent pseudo-random values based on coordinates and octave
                    double nx = x * scale * frequency;
                    double ny = y * scale * frequency;
                    
                    // Generate coherent noise value (improved from original sine/cosine approximation)
                    double noiseValue = perlinNoise2D(nx, ny, seed + i);
                    
                    // Add weighted noise to the result
                    noise += noiseValue * amplitude;
                    
                    // Track the maximum possible value
                    maxValue += amplitude;
                    
                    // Prepare for next octave
                    amplitude *= persistence;
                    frequency *= 2;
                }
                
                // Normalize to [0,1] range
                double t = noise / maxValue;
                
                // Map to [0,1] range more precisely
                t = (t + 1) * 0.5;
                
                // Apply requested easing curve
                double adjustedT = applyEasingCurve(t, curveType);
                
                // Apply additional noise if requested 
                if (noiseLevel > 0) {
                    // Use coherent random value based on position to maintain pattern consistency
                    double extraNoise = (perlinNoise2D(x * 0.7, y * 0.7, seed + 1000) - 0.5) * 2 * noiseLevel;
                    adjustedT = Math.max(0, Math.min(1, adjustedT + extraNoise));
                }
                
                // Get the interpolated block based on the noise value
                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, adjustedT, useTopTexture);
            }
        }
    }

    /**
     * Generate 2D Perlin noise at the given coordinates
     * This is a proper implementation replacing the simple sine/cosine approximation
     */
    private double perlinNoise2D(double x, double y, long seed) {
        // Integer coordinates
        int x0 = (int)Math.floor(x);
        int y0 = (int)Math.floor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        
        // Fractional coordinates
        double sx = x - x0;
        double sy = y - y0;
        
        // Smoothly interpolate between grid points
        double n0 = dotGridGradient(x0, y0, x, y, seed);
        double n1 = dotGridGradient(x1, y0, x, y, seed);
        double ix0 = smoothInterpolate(n0, n1, sx);
        
        double n2 = dotGridGradient(x0, y1, x, y, seed);
        double n3 = dotGridGradient(x1, y1, x, y, seed);
        double ix1 = smoothInterpolate(n2, n3, sx);
        
        return smoothInterpolate(ix0, ix1, sy);
    }

    /**
     * Calculate dot product of gradient and distance vectors
     */
    private double dotGridGradient(int ix, int iy, double x, double y, long seed) {
        // Get gradient vector for grid point
        double[] gradient = getRandomGradient(ix, iy, seed);
        
        // Distance vector from grid point to (x,y)
        double dx = x - ix;
        double dy = y - iy;
        
        // Dot product
        return dx * gradient[0] + dy * gradient[1];
    }

    /**
     * Get a deterministic random gradient vector for the given coordinates
     */
    private double[] getRandomGradient(int x, int y, long seed) {
        // Create a deterministic hash for the coordinates
        int hash = (x * 73856093) ^ (y * 19349663) ^ (int)seed;
        hash = hash & 0x7fffffff; // Ensure positive
        
        // Convert hash to angle
        double angle = (hash % 1000) / 1000.0 * 2 * Math.PI;
        
        // Create normalized gradient vector
        return new double[] {Math.cos(angle), Math.sin(angle)};
    }

    /**
     * Smooth interpolation function (improved from linear)
     */
    private double smoothInterpolate(double a, double b, double t) {
        // Use improved smoothstep function for more natural transitions
        t = t * t * (3 - 2 * t);
        return a + t * (b - a);
    }

    /**
     * Generates a Voronoi pattern
     */
    private void generateVoronoiPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                     int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        // Generate random points for Voronoi cells
        int points = 10;
        double[][] seeds = new double[points][2];
        for (int i = 0; i < points; i++) {
            seeds[i][0] = Math.random() * size;
            seeds[i][1] = Math.random() * size;
        }

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double minDist = Double.POSITIVE_INFINITY;
                double t = 0;

                for (int i = 0; i < points; i++) {
                    double dx = x - seeds[i][0];
                    double dy = y - seeds[i][1];
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < minDist) {
                        minDist = dist;
                        t = (double) i / (points - 1); // Linear interpolation based on point index
                    }
                }

                double adjustedT = applyEasingCurve(t, curveType);
                if (noiseLevel > 0) {
                    double noise = (Math.random() - 0.5) * 2 * noiseLevel;
                    adjustedT = Math.max(0, Math.min(1, adjustedT + noise));
                }

                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, adjustedT, useTopTexture);
            }
        }
    }


    /**
     * Generates a Cellular pattern
     */
    private void generateCellularPattern(Block[][] pattern, Block startBlock, Block endBlock,
                                      int size, double noiseLevel, CurveType curveType, boolean useTopTexture) {
        int points = 20;
        double[][] seeds = new double[points][2];
        for (int i = 0; i < points; i++) {
            seeds[i][0] = Math.random() * size;
            seeds[i][1] = Math.random() * size;
        }

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                double minDist = Double.POSITIVE_INFINITY;
                double t = 0;

                for (int i = 0; i < points; i++) {
                    double dx = x - seeds[i][0];
                    double dy = y - seeds[i][1];
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < minDist) {
                        minDist = dist;
                        t = Math.min(1.0, dist / (size / 2)); // Normalize distance
                    }
                }

                double adjustedT = applyEasingCurve(1 - t, curveType); // Invert for cellular effect
                if (noiseLevel > 0) {
                    double noise = (Math.random() - 0.5) * 2 * noiseLevel;
                    adjustedT = Math.max(0, Math.min(1, adjustedT + noise));
                }

                pattern[y][x] = getInterpolatedBlock(startBlock, endBlock, adjustedT, useTopTexture);
            }
        }
    }

    /**
     * Apply easing curve to the transition
     */
    private double applyEasingCurve(double t, CurveType curveType) {
        switch (curveType) {
            case EASE_IN:
                return t * t; // Quadratic ease in
            case EASE_OUT:
                return 1 - Math.pow(1 - t, 2); // Quadratic ease out
            case EASE_IN_OUT:
                return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2; // Quadratic ease in-out
            case LINEAR:
            default:
                return t; // Linear (no easing)
        }
    }

    /**
     * Get interpolated block between start and end blocks based on t parameter (0 to 1)
     */
    private Block getInterpolatedBlock(Block startBlock, Block endBlock, double t, boolean useTopTexture) {
        // For exact endpoints, return those blocks
        if (t <= 0.0) return startBlock;
        if (t >= 1.0) return endBlock;
    
        // Get average colors
        Color startColor = useTopTexture ? startBlock.getAverageTopColor() : startBlock.getAverageSideColor();
        Color endColor = useTopTexture ? endBlock.getAverageTopColor() : endBlock.getAverageSideColor();
    
        if (startColor == null || endColor == null) {
            // If still null, use black as fallback
            if (startColor == null) startColor = Color.BLACK;
            if (endColor == null) endColor = Color.BLACK;
        }
    
        // Interpolate color
        int r = (int) (startColor.getRed() + t * (endColor.getRed() - startColor.getRed()));
        int g = (int) (startColor.getGreen() + t * (endColor.getGreen() - startColor.getGreen()));
        int b = (int) (startColor.getBlue() + t * (endColor.getBlue() - startColor.getBlue()));
    
        Color interpolatedColor = new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b))
        );
    
        // Find the closest matching block by color
        return TextureManager.getInstance().findClosestColorBlock(interpolatedColor, useTopTexture);
    }
    

    /**
     * Get all available pattern types
     */
    public List<PatternType> getAvailablePatternTypes() {
        List<PatternType> types = new ArrayList<>();
        for (PatternType type : PatternType.values()) {
            types.add(type);
        }
        return types;
    }

    /**
     * Get all available curve types
     */
    public List<CurveType> getAvailableCurveTypes() {
        List<CurveType> types = new ArrayList<>();
        for (CurveType type : CurveType.values()) {
            types.add(type);
        }
        return types;
    }
}