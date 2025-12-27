package me.lotiny.misty.bukkit.utils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class TimeFormatUtils {

    /**
     * Format a time duration in seconds into HH:mm:ss format.
     *
     * @param timer The time duration in seconds.
     * @return A formatted time string (e.g., "01:00:00").
     */
    public String formatTime(int timer) {
        int hours = timer / 3600;
        int secondsLeft = timer - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;
        String formattedTime = "";
        if (hours > 0) {
            if (hours < 10) {
                formattedTime = formattedTime + "0";
            }
            formattedTime = formattedTime + hours + ":";
        }

        if (minutes < 10) {
            formattedTime = formattedTime + "0";
        }

        formattedTime = formattedTime + minutes + ":";
        if (seconds < 10) {
            formattedTime = formattedTime + "0";
        }

        formattedTime = formattedTime + seconds;
        return formattedTime;
    }

    /**
     * Format a time duration in milliseconds into a human-readable format.
     *
     * @param duration The time duration in milliseconds.
     * @return A formatted time string (e.g., "1y,30d,5h,30m,10s").
     */
    public String formatTime(long duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Parse a time input string and convert it to milliseconds.
     *
     * @param input The time input string (e.g., "1h30m").
     * @return The time duration in milliseconds, or -1L if parsing fails.
     */
    public long handleParseTime(String input) {
        if (Character.isLetter(input.charAt(0))) {
            return -1L;
        } else {
            long result = 0L;
            StringBuilder number = new StringBuilder();

            for (int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                if (Character.isDigit(c)) {
                    number.append(c);
                } else {
                    String str;
                    if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                        result += handleConvert(Integer.parseInt(str), c);
                        number = new StringBuilder();
                    }
                }
            }

            return result;
        }
    }

    private long handleConvert(int value, char charType) {
        return switch (charType) {
            case 'M' -> (long) value * TimeUnit.DAYS.toMillis(30L);
            case 'd' -> (long) value * TimeUnit.DAYS.toMillis(1L);
            case 'h' -> (long) value * TimeUnit.HOURS.toMillis(1L);
            case 'm' -> (long) value * TimeUnit.MINUTES.toMillis(1L);
            case 's' -> (long) value * TimeUnit.SECONDS.toMillis(1L);
            case 'w' -> (long) value * TimeUnit.DAYS.toMillis(7L);
            case 'y' -> (long) value * TimeUnit.DAYS.toMillis(365L);
            default -> -1L;
        };
    }

    /**
     * Format a time duration in seconds into a human-readable format.
     *
     * @param seconds The time duration in seconds.
     * @return A formatted time string (e.g., "1h 30m 10s").
     */
    public String formatTimeSeconds(int seconds) {
        if (seconds <= 0) {
            return "0 seconds";
        }

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        StringBuilder formattedTime = new StringBuilder();

        if (hours > 0) {
            formattedTime.append(hours).append("h ");
        }

        if (minutes > 0) {
            formattedTime.append(minutes).append("m ");
        }

        if (remainingSeconds > 0) {
            formattedTime.append(remainingSeconds).append("s");
        }

        return formattedTime.toString().trim();
    }

    /**
     * Format a time duration in milliseconds into a human-readable format.
     *
     * @param millis The time duration in milliseconds.
     * @return A formatted time string (e.g., "1y 30d 5h 30m 10s").
     */
    public String formatTimeMillis(long millis) {
        if (millis <= 0) {
            return "0 seconds";
        }

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long years = days / 365;

        StringBuilder formattedTime = new StringBuilder();

        if (years > 0) {
            formattedTime.append(years).append("y ");
        }

        if (days > 0) {
            formattedTime.append(days).append("d ");
        }

        if (hours > 0) {
            formattedTime.append(hours).append("h ");
        }

        if (minutes > 0) {
            formattedTime.append(minutes).append("m ");
        }

        if (seconds > 0) {
            formattedTime.append(seconds % 60).append("s");
        }

        return formattedTime.toString().trim();
    }

    public String formatTimeUnit(int seconds) {
        if (seconds >= 60) {
            int minutes = seconds / 60;
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        } else {
            return seconds + " second" + (seconds > 1 ? "s" : "");
        }
    }
}
