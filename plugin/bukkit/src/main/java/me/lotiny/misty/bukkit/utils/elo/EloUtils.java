package me.lotiny.misty.bukkit.utils.elo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EloUtils {

    private final KFactor[] K_FACTORS = {
            new KFactor(0, 1000, 40),   // Beginner
            new KFactor(1001, 1400, 45),
            new KFactor(1401, 1800, 35),
            new KFactor(1801, 2200, 25) // Advanced
    };

    private final int DEFAULT_K_FACTOR = 15; // fallback for very high ratings
    private final int WIN = 1;
    private final int LOSS = 0;

    /**
     * Calculate the new rating for a player after a match.
     *
     * @param rating         The player's current rating.
     * @param opponentRating The opponent's rating.
     * @param won            True if the player won, false if lost.
     * @return The new rating for the player.
     */
    public int getNewRating(int rating, int opponentRating, boolean won) {
        return getNewRating(rating, opponentRating, won ? WIN : LOSS);
    }

    /**
     * Calculate the new rating for a player after a match.
     *
     * @param rating         The player's current rating.
     * @param opponentRating The opponent's rating.
     * @param score          The score representing the outcome of the match
     *                       (1 for win, 0 for loss, 0.5 for draw).
     * @return The new rating for the player.
     */
    public int getNewRating(int rating, int opponentRating, double score) {
        double kFactor = getKFactor(rating);
        double expectedScore = getExpectedScore(rating, opponentRating);

        int newRating = rating + (int) Math.round(kFactor * (score - expectedScore));

        // Safeguard: if a player wins and rating didn't increase, bump by 1
        if (score == 1 && newRating == rating) {
            newRating++;
        }

        return newRating;
    }

    /**
     * Get the K-factor (weight) to use based on a player's rating.
     *
     * @param rating The player's rating.
     * @return The appropriate K-factor value.
     */
    private double getKFactor(int rating) {
        for (KFactor factor : K_FACTORS) {
            if (rating >= factor.getStartIndex() && rating <= factor.getEndIndex()) {
                return factor.getValue();
            }
        }
        return DEFAULT_K_FACTOR;
    }

    /**
     * Calculate the expected score of a player in a match against an opponent.
     *
     * @param rating         The player's rating.
     * @param opponentRating The opponent's rating.
     * @return The expected score based on the Elo formula.
     */
    private double getExpectedScore(int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10, (opponentRating - rating) / 400.0));
    }

    @Getter
    @RequiredArgsConstructor
    public static class KFactor {
        private final int startIndex;
        private final int endIndex;
        private final double value;
    }

}
