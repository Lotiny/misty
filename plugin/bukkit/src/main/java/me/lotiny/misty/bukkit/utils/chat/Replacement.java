package me.lotiny.misty.bukkit.utils.chat;

import io.fairyproject.util.CC;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Replacement {

    private Map<Object, Object> replacements = new HashMap<>();
    private String message = "";

    /**
     * Create a `Replacement` object with the specified message.
     *
     * @param message The original message to which replacements will be applied.
     */
    public Replacement(String message) {
        this.message = message;
    }

    /**
     * Add a replacement mapping to replace occurrences of `current` with `replacement`.
     *
     * @param current     The value to be replaced.
     * @param replacement The value to replace `current` with.
     */
    public void add(Object current, Object replacement) {
        replacements.put(current, replacement);
    }

    /**
     * Add a replacement mapping with a default value.
     *
     * @param current      The value to be replaced.
     * @param replacement  The value to replace `current` with.
     * @param defaultValue The value to use if `replacement` is null.
     */
    public void add(Object current, Object replacement, Object defaultValue) {
        replacements.put(current, replacement != null ? replacement : defaultValue);
    }

    /**
     * Add all replacement mappings from another `Replacement` object to this one.
     *
     * @param replacement The `Replacement` object containing replacement mappings to be added.
     */
    public void addAll(Replacement replacement) {
        replacements.putAll(replacement.getReplacements());
    }

    /**
     * Apply the replacements to the message and return the result as a translated string.
     *
     * @return The message with replacements applied and translated.
     */
    public String toString() {
        replacements.keySet().forEach(current -> this.message = this.message.replace(String.valueOf(current), String.valueOf(replacements.get(current))));
        return CC.translate(this.message);
    }
}
