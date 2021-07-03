package com.taka.pricer;

import java.util.Random;

public class RandomGenerator {
    private final Random random;

    public RandomGenerator(int seed) {
        random = new Random(seed);
    }

    // Box–Muller transform from a uniform random number to a gaussian random number.
    public double getNum() {
        double r, u, v;
        do {
            // TODO: This is not a pseudo-random sequence like Sobol, which would have a better convergence.
            u = 2.0 * random.nextDouble() - 1.0;
            v = 2.0 * random.nextDouble() - 1.0;
            r = u * u + v * v;
        } while (r > 1 || r == 0);
        return u * Math.sqrt(-2.0 * Math.log(r) / r);
    }
}
