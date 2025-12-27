package me.lotiny.misty.api.team.invitation;

import io.fairyproject.util.Cooldown;

import java.util.function.Consumer;

public class TeamInvitationCooldown extends Cooldown<TeamInvitation> {

    public TeamInvitationCooldown(long defaultCooldown, Consumer<TeamInvitation> removalListener) {
        super(defaultCooldown, removalListener);
    }
}
