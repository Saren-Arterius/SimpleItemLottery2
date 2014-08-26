package net.wtako.SILOT2.Utils;

public interface BaseCommands {

    public String getHelpMessage();

    public String name();

    public Class<?> getTargetClass();

    public String getRequiredPermission();

}
