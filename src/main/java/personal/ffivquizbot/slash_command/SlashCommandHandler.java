package personal.ffivquizbot.slash_command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandHandler {
    public abstract void handleCommand(SlashCommandInteractionEvent event);
}
