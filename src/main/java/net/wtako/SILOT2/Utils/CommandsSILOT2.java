package net.wtako.SILOT2.Utils;

import net.wtako.SILOT2.Main;
import net.wtako.SILOT2.Commands.silot2.ArgAdd;
import net.wtako.SILOT2.Commands.silot2.ArgDel;
import net.wtako.SILOT2.Commands.silot2.ArgHelp;
import net.wtako.SILOT2.Commands.silot2.ArgList;
import net.wtako.SILOT2.Commands.silot2.ArgMake;
import net.wtako.SILOT2.Commands.silot2.ArgReload;

public enum CommandsSILOT2 implements BaseCommands {

    MAIN_COMMAND(Lang.HELP_HELP.toString(), ArgHelp.class, Main.artifactId + ".use"),
    ADD(Lang.HELP_ADD.toString(), ArgAdd.class, Main.artifactId + ".admin"),
    DEL(Lang.HELP_DELETE.toString(), ArgDel.class, Main.artifactId + ".admin"),
    LIST(Lang.HELP_LIST.toString(), ArgList.class, Main.artifactId + ".list"),
    MAKE(Lang.HELP_MAKE.toString(), ArgMake.class, Main.artifactId + ".admin"),
    H(Lang.HELP_HELP.toString(), ArgHelp.class, Main.artifactId + ".use"),
    HELP(Lang.HELP_HELP.toString(), ArgHelp.class, Main.artifactId + ".use"),
    RELOAD(Lang.HELP_RELOAD.toString(), ArgReload.class, Main.artifactId + ".reload");

    private String   helpMessage;
    private Class<?> targetClass;
    private String   permission;

    private CommandsSILOT2(String helpMessage, Class<?> targetClass, String permission) {
        this.helpMessage = helpMessage;
        this.targetClass = targetClass;
        this.permission = permission;
    }

    @Override
    public String getHelpMessage() {
        return helpMessage;
    }

    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }

    @Override
    public String getRequiredPermission() {
        return permission;
    }
}
